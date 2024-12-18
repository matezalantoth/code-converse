import {Component, Input} from '@angular/core';
import {Marked} from "marked";
import {markedHighlight} from "marked-highlight";
import hljs from 'highlight.js';

@Component({
  selector: 'app-markdown-preview',
  templateUrl: './markdown-preview.component.html',
  styleUrl: './markdown-preview.component.css'
})
export class MarkdownPreviewComponent {
  @Input()
  markdownInput: string = '';
  private marked: Marked = new Marked(
    markedHighlight({
      emptyLangClass: 'hljs',
      langPrefix: 'hljs language-',
      highlight(code, lang, info) {

        const language = hljs.getLanguage(lang) ? lang : 'plaintext';
        return hljs.highlight(code, {language}).value;
      }
    }))


  get markdownPreview(): string | Promise<any> {
    return this.marked.parse(this.markdownInput);
  }
}
