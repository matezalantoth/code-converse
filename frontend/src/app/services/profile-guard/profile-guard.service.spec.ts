import { TestBed } from '@angular/core/testing';

import { ProfileGuardService } from './profile-guard.service';

describe('ProfileGuardService', () => {
  let service: ProfileGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfileGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
