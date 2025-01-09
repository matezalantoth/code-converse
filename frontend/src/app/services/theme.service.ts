import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ThemeService {
  private darkModeKey = 'dark-mode-enabled';

  constructor() {
    if (this.isDarkModeEnabled()) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }

  isDarkModeEnabled(): boolean {
    return localStorage.getItem(this.darkModeKey) === 'true';
  }

  toggleDarkMode(): void {
    const isDarkMode = this.isDarkModeEnabled();
    if (isDarkMode) {
      document.documentElement.classList.remove('dark');
      localStorage.setItem(this.darkModeKey, 'false');
    } else {
      document.documentElement.classList.add('dark');
      localStorage.setItem(this.darkModeKey, 'true');
    }
  }
}
