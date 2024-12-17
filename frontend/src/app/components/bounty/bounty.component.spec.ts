import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BountyComponent } from './bounty.component';

describe('BountyComponent', () => {
  let component: BountyComponent;
  let fixture: ComponentFixture<BountyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BountyComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BountyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
