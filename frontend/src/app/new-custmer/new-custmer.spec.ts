import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewCustmer } from './new-custmer';

describe('NewCustmer', () => {
  let component: NewCustmer;
  let fixture: ComponentFixture<NewCustmer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewCustmer],
    }).compileComponents();

    fixture = TestBed.createComponent(NewCustmer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
