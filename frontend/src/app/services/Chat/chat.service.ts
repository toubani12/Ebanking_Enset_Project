import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatRequest {
  query: string;
  conversationId: string;
}

export interface ChatResponse {
  response: string;
}

@Injectable({ providedIn: 'root' })
export class ChatService {

  private readonly apiUrl = 'http://localhost:8080/ai/chat';

  constructor(private http: HttpClient) {}

  askAgent(query: string, conversationId: string): Observable<ChatResponse> {
    const payload: ChatRequest = { query, conversationId };
    return this.http.post<ChatResponse>(this.apiUrl, payload);
  }
}
