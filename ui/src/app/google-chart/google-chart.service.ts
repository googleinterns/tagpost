import { Injectable } from '@angular/core';

declare var google: any;

/**
 * Service to access Google Chart library
 */
@Injectable({
  providedIn: 'root'
})
export class GoogleChartService {

  private readonly google: any;
  private loaderPromise: Promise<any>;
  number = 1;

  constructor() {
    this.google = google;
  }

  getGoogle(): any {
    return this.google;
  }

  loadChart(): Promise<any> {
    // Load Google Chart library only when library is not loaded.
    if (!this.loaderPromise) {
      this.loaderPromise = google.charts.load('current', { packages: ['corechart'] });
    }
    return this.loaderPromise;
  }
}
