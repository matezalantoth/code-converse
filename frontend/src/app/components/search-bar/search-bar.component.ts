import {Component, OnInit, Renderer2} from '@angular/core';
import {ApiService} from "../../services/data/api.service";
import {NavigationService} from "../../services/nav/nav.service";

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css'
})
export class SearchBarComponent implements OnInit {

  public tags: any[] = [];
  public tagResults: any[] = [];
  public tagSelectIndex: number = 0;
  public inputted: any = "";
  public tagSelect: boolean = false;
  public showDropdown: boolean = false;
  private documentClickListener!: () => void;

  constructor(private api: ApiService, private renderer: Renderer2, private nav: NavigationService) {

  }

  ngOnInit() {
    this.documentClickListener = this.renderer.listen('document', 'click', (event: MouseEvent) => {
      this.showDropdown = (event.target as HTMLElement).className.includes("searchbar");

    });
  }

  search() {
    let tagsString = "";
    this.tags.forEach(t => tagsString += t.tag.name + ',');
    tagsString = tagsString.slice(0, tagsString.length - 1);
    const searchObj = {tagNames: tagsString, content: this.inputted, startIndex: 1};
    localStorage.setItem('searchJSON', JSON.stringify(searchObj));
    this.showDropdown = false;
    this.nav.redirectToSearchResults();

  }

  handleTagSelect(tag: any) {
    this.inputted = this.inputted.substring(0, this.tagSelectIndex - 2);
    this.tags.push(tag);
    this.tagResults = [];
  }

  handleInput(event: InputEvent) {
    if (!this.inputted.includes('[')) {
      this.tagSelect = false;
      this.tagResults = [];
    }
    if (event.data === '[') {
      this.tagSelect = true;
      this.tagSelectIndex = this.inputted.length + 2;
    }
    if (event.data === ']' && this.tagSelect) {
      const inputtedCopy = this.inputted;
      this.addTag(inputtedCopy.substring(this.tagSelectIndex - 1));
      this.tagSelect = false;
      this.tagResults = [];
      this.inputted = this.inputted.substring(0, this.tagSelectIndex - 2);
      event.preventDefault();
    }
    if (event.data === '\\') {
      this.tagSelect = false;
      this.tagResults = [];
      event.preventDefault();
    }
  }

  registerInput(event: Event) {
    const val = (event.target as HTMLInputElement).value;
    if (val[val.length - 1] === '[') {
      this.tagSelect = true;
      this.tagSelectIndex = this.inputted.length + 2;
    }
    this.inputted = (event.target as HTMLInputElement).value;
    if (this.tagSelect) {
      let tagSearch = this.inputted.substring(this.tagSelectIndex);
      this.getResults(tagSearch)
    }
  }

  addTag(name: string) {
    const tag = this.tagResults.find(t => {
      return t.tag.name.toLowerCase() === name.toLowerCase()
    });
    if (tag !== undefined) {
      this.tags.push(tag);
    }
  }


  removeTag(tag: any) {
    this.tags = this.tags.filter(t => t.tag.id != tag.tag.id);
  }

  getResults(tagName: string) {
    this.api.tagsAutocomplete(tagName, this.tags).subscribe((res) => {
      this.tagResults = res;
    })
  }


}
