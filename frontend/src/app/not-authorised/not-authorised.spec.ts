import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotAuthorised } from './not-authorised';

describe('NotAuthorised', () => {
  let component: NotAuthorised;
  let fixture: ComponentFixture<NotAuthorised>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotAuthorised],
    }).compileComponents();

    fixture = TestBed.createComponent(NotAuthorised);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
