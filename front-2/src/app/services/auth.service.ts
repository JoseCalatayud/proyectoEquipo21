import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<any>(JSON.parse(localStorage.getItem('currentUser') || 'null'));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue() {
    return this.currentUserSubject.value;
  }

  login(username: string, password: string): Observable<any> {
    // Crear el header para Auth Basic
    const headers = new HttpHeaders({
      'Authorization': 'Basic ' + btoa(username + ':' + password)
    });

    return this.http.post<any>(`/api/login`, {}, { headers })
      .pipe(
        tap(user => {
          // Almacenar los datos del usuario y credenciales
          const userData = {
            username,
            password,
            ...user
          };
          localStorage.setItem('currentUser', JSON.stringify(userData));
          this.currentUserSubject.next(userData);
        })
      );
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  isAuthenticated(): boolean {
    return this.currentUserValue !== null;
  }
}
