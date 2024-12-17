import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BountyAwarenessComponent } from './bounty-awareness.component';

describe('BountyAwarenessComponent', () => {
  let component: BountyAwarenessComponent;
  let fixture: ComponentFixture<BountyAwarenessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BountyAwarenessComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BountyAwarenessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
