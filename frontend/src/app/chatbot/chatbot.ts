import {
  AfterViewChecked,
  Component,
  ElementRef,
  inject,
  OnInit,
  PLATFORM_ID,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';
import { MarkdownComponent } from 'ngx-markdown';
import { ChatService } from '../services/Chat/chat.service';
import { AuthService } from '../services/Auth/auth-service';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [FormsModule, MarkdownComponent],
  templateUrl: './chatbot.html',
  styleUrl: './chatbot.css',
})
export class Chatbot implements OnInit, AfterViewChecked {

  @ViewChild('scrollAnchor') private scrollAnchor!: ElementRef;

  messages: ChatMessage[] = [];
  userInput = '';
  isLoading = false;
  conversationId = '';

  private platformId = inject(PLATFORM_ID);

  constructor(
    private chatService: ChatService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Isolate memory per logged-in user on the backend
    this.conversationId = this.authService.uaername || this.randomId();
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  sendMessage(): void {
    const query = this.userInput.trim();
    if (!query || this.isLoading) return;

    this.messages.push({ role: 'user', content: query });
    this.userInput = '';
    this.isLoading = true;

    this.chatService.askAgent(query, this.conversationId).subscribe({
      next: (res) => {
        this.messages.push({ role: 'assistant', content: res.response });
        this.isLoading = false;
      },
      error: () => {
        this.messages.push({
          role: 'assistant',
          content: "Désolé, une erreur s'est produite. Veuillez réessayer.",
        });
        this.isLoading = false;
      },
    });
  }

  private scrollToBottom(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    try {
      this.scrollAnchor?.nativeElement.scrollIntoView({ behavior: 'smooth' });
    } catch {}
  }

  private randomId(): string {
    return Math.random().toString(36).slice(2, 11);
  }
}
