import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {debounceTime, Observable, Subject} from "rxjs";

@Component({
  selector: 'app-infinite-scroll',
  templateUrl: './infinite-scroll.component.html',
  styleUrl: './infinite-scroll.component.css'
})
export class InfiniteScrollComponent implements OnInit, AfterViewInit {

  @Input()
  fetchItems!: () => Promise<boolean>;
  @Input()
  select: Observable<any> | null = null;
  private observer!: IntersectionObserver;
  @ViewChild('scrollAnchor', {static: false}) scrollAnchor!: ElementRef;
  private scrollSubject = new Subject<void>();
  private enabled: boolean = true;
  private first: boolean = true;

  ngOnInit() {
    this.scrollSubject.subscribe(() => {
      if (this.first) {
        this.first = false;
        return;
      }
      if (this.enabled) {
        this.fetchItems().then(res => this.enabled = res);
      }
    })
    if (this.select !== null) {
      this.select.subscribe(() => {
        this.enabled = true;
        this.first = true;
      })
    }
  }

  ngAfterViewInit() {
    this.observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          this.scrollSubject.next();
        }
      },
      {threshold: 1.0}
    );

    this.observer.observe(this.scrollAnchor.nativeElement);
  }
}
