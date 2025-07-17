import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { PreferenceService } from '../../services/preference.service';
import { RoadmapService } from '../../services/roadmap';
import { City, UserPreferenceResponse } from '../../models/preferences.models';
import { LocalResponse, CreateRoadmapRequest } from '../../models/roadmap.models';
import { Review } from '../../models/reviews.models';
import * as L from 'leaflet';

const delay = (ms: number) => new Promise(res => setTimeout(res, ms));

const blueIcon = L.icon({
  iconUrl: 'img/marker-icon-blue.png',
  iconRetinaUrl: 'img/marker-icon-2x-blue.png',
  shadowUrl: '',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [0, 0],
});

const redIcon = L.icon({
  iconUrl: 'img/marker-icon-red.png',
  iconRetinaUrl: 'img/marker-icon-2x-red.png',
  shadowUrl: '',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [0, 0],
});

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  completeConfig = false;
  cities: City[] = [];
  setupForm: FormGroup;
  private map: L.Map | null = null;
  private markerLayer = L.layerGroup();
  userPreferences: UserPreferenceResponse | null = null;
  roadmapForm: FormGroup;
  suggestedLocales: LocalResponse[] = [];
  availableCategories = ['RESTAURANTE', 'MUSEU', 'BAR', 'CINEMA', 'PARQUE'];
  isReviewModalOpen = false;
  localToReview: LocalResponse | null = null;
  reviewForm: FormGroup;
  private markersMap = new Map<number, L.Marker>();

  constructor (private fb: FormBuilder, private preferenceService: PreferenceService, private roadmapService: RoadmapService) {
    this.setupForm = this.fb.group ({city: ['', Validators.required], distance: [10, Validators.required]});
    this.roadmapForm = this.fb.group({categories: this.fb.array([], Validators.required)});
    this.reviewForm = this.fb.group({
      rating: [0, [Validators.required, Validators.min(1)]],
      comment: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.preferenceService.getCities().subscribe(cities => {
      this.cities = cities;
    });
    this.preferenceService.getPreferenceStatus().subscribe({
      next: (response) => {
        if (response.status === 200 && response.body) {
          this.userPreferences = response.body;
          this.showAndInitializeMap();
        } else {
          this.completeConfig = false;
        }
      },
      error: (err) => {
        this.completeConfig = false;
      }
    });
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  async showAndInitializeMap() {
    this.completeConfig = true;
    await delay(50);
    this.initMap();
  }

  private initMap(): void {
    if (this.map) {
      this.centerMapOnUser();
      return;
    }
    this.map = L.map('map').setView([-14.2350, -51.9253], 5);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);
    this.markerLayer.addTo(this.map);
    this.map.whenReady(() => {
        this.map?.invalidateSize();
        this.centerMapOnUser();
    });
  }

  private centerMapOnUser(): void {
    if (!this.map || !this.userPreferences || this.cities.length === 0) {
      return;
    }
    const city = this.cities.find(c => c.name === this.userPreferences?.cityName);
    if (city) {
      const lat = city.latitude;
      const lng = city.longitude;
      this.map.setView([lat, lng], 12);
      this.map.invalidateSize();
      this.map.eachLayer((layer) => {
        if (layer instanceof L.Marker) {
          layer.remove();
        }
      });
      L.marker([lat, lng], { icon: blueIcon })
        .addTo(this.map)
        .bindPopup('<b>Você está aqui</b>')
        .openPopup();
    }
  }

  onSubmitSetup(): void {
    if (this.setupForm.invalid) return;
    const dataToSave = {
      cityId: this.setupForm.value.city, distance: this.setupForm.value.distance
    };
    this.preferenceService.savePreferences(dataToSave).subscribe({
      next: (prefs) => {
        this.userPreferences = prefs;
        this.showAndInitializeMap();
      }
    });
  }

  onCategoryChange(event: any): void {
    const categoriesArray: FormArray = this.roadmapForm.get('categories') as FormArray;
    if (event.target.checked) {
      categoriesArray.push(this.fb.control(event.target.value));
    }
    else {
      const index = categoriesArray.controls.findIndex(x => x.value === event.target.value);
      categoriesArray.removeAt(index);
    }
  }

  onSubmitRoadmap(): void {
    if (this.roadmapForm.invalid) return;
    const requestData: CreateRoadmapRequest = {
      categories: this.roadmapForm.value.categories
    };
    this.roadmapService.getSuggestions(requestData).subscribe(locales => {
      this.suggestedLocales = locales;
      this.updateMapMarkers();
    });
  }

  private openPopupWithReviews(local: LocalResponse, marker: L.Marker): void {
    this.roadmapService.getReviewsForLocal(local.id).subscribe({
      next: (reviews) => {
        const popupContent = this.createPopupContent(local, reviews);
        marker.bindPopup(popupContent).openPopup();

        setTimeout(() => {
          const button = document.getElementById('add-review-btn');
          if (button) {
            button.addEventListener('click', () => {
              this.openReviewModal(local);
            });
          }
        }, 0);
      },
      error: (err) => {
        marker.bindPopup('<b>Erro ao carregar avaliações.</b>').openPopup();;
      }
    });
  }

  onLocalMarkerClick(local: LocalResponse, marker: L.Marker): void {
    this.openPopupWithReviews(local, marker);
  }

  onSidebarLocalClick(local: LocalResponse): void {
    if (!this.map) return;
    const marker = this.markersMap.get(local.id);
    if (marker) {
      this.map.panTo(marker.getLatLng());
      this.openPopupWithReviews(local, marker);
    }
  }

  createPopupContent(local: LocalResponse, reviews: Review[]): string {
    let reviewsHtml = '';
    let titleHtml = `<h3>${local.name}</h3>`;
    if (local.averageRating && local.averageRating > 0) {
      titleHtml = `<h3>${local.name} <span class="popup-rating">(${local.averageRating.toFixed(1)} ★)</span></h3>`;
    }
    if (reviews.length > 0) {
      reviews.forEach(review => {
        reviewsHtml += `
          <div class="comment-item">
            <strong>${review.userName} ${review.rating} ★</strong>
            <p class="comment-text">${review.comment}</p>
          </div>
        `;
      });
    }
    const addReviewButtonHtml = `
      <div class="add-review-section">
        <hr>
        <p>Já visitou esse local?</p>
        <button id="add-review-btn" class="btn-add-review">Deixe uma avaliação</button>
      </div>
    `;
    return `
      <div class="review-popup-content">
        ${titleHtml}
        <p class="details-description">${local.description}</p>
        <div class="comments-list">
          <h4>Avaliações</h4>
          ${reviewsHtml.length > 0 ? reviewsHtml : '<p class="empty-state">Este local ainda não possui avaliações.</p>'}
        </div>
        ${addReviewButtonHtml}
      </div>
    `;
  }

  updateMapMarkers(): void {
    if (!this.map) return;
    this.markerLayer.clearLayers();
    this.markersMap.clear();
    this.suggestedLocales.forEach(local => {
      const marker = L.marker([local.latitude, local.longitude], { icon: redIcon });
      marker.on('click', () => {
        this.onLocalMarkerClick(local, marker);
      });
      this.markerLayer.addLayer(marker);
      this.markersMap.set(local.id, marker);
    });
  }

  openReviewModal(local: LocalResponse): void {
    this.localToReview = local;
    this.isReviewModalOpen = true;
    this.reviewForm.reset({ rating: 0, comment: '' });
  }

  closeReviewModal(): void {
    this.isReviewModalOpen = false;
    this.localToReview = null;
  }

  setRating(rating: number): void {
    this.reviewForm.get('rating')?.setValue(rating);
  }

  submitReview(): void {
    if (this.reviewForm.invalid || !this.localToReview) {
      return;
    }
    const reviewData = {
      rating: this.reviewForm.value.rating,
      comment: this.reviewForm.value.comment
    };
    this.roadmapService.addReview(this.localToReview.id, reviewData).subscribe({
      next: (newReview) => {
        alert('Avaliação enviada com sucesso!');
        this.closeReviewModal();
      },
      error: (err) => {
        console.error('Erro ao enviar avaliação', err);
        alert('Não foi possível enviar sua avaliação. Tente novamente.');
      }
    });
  }
}
