import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormArray } from '@angular/forms';
import { PreferenceService } from '../../services/preference.service';
import { RoadmapService } from '../../services/roadmap';
import { City, UserPreferenceResponse } from '../../models/preferences.models';
import { LocalResponse, CreateRoadmapRequest } from '../../models/roadmap.models';

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

  constructor (private fb: FormBuilder, private preferenceService: PreferenceService, private roadmapService: RoadmapService) {
    this.setupForm = this.fb.group ({city: ['', Validators.required], distance: [10, Validators.required]});
    this.roadmapForm = this.fb.group({categories: this.fb.array([], Validators.required)});
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

  updateMapMarkers(): void {
    if (!this.map) return;
    this.markerLayer.clearLayers();

    this.suggestedLocales.forEach(local => {
      const marker = L.marker([local.latitude, local.longitude], { icon: redIcon }).bindPopup(`<b>${local.name}</b><br>${local.description}`);
      this.markerLayer.addLayer(marker);
    });
  }
}
