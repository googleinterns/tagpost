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

  constructor() {
    this.google = google;
  }

  getGoogle(): any {
    return this.google;
  }
}
