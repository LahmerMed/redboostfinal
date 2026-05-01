import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { AppMenuitem } from './app.menuitem';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, AppMenuitem, RouterModule],
  template: `
    <ul class="layout-menu">
      <ng-container *ngFor="let item of model; let i = index">
        <li app-menuitem *ngIf="!item.separator" [item]="item" [index]="i" [root]="true"></li>
        <li *ngIf="item.separator" class="menu-separator"></li>
      </ng-container>
    </ul>
  `,
})
export class AppMenu implements OnInit {
  model: MenuItem[] = [];

  constructor(
    private http: HttpClient,
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.fetchUserRoleAndSetMenu();
  }

  // Fetch user role from the backend and set the menu
  private fetchUserRoleAndSetMenu(): void {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'No authentication token found. Please log in.',
      });
      this.router.navigate(['/signin']);
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    this.http.get('http://localhost:8085/users/profile', { headers }).subscribe({
      next: (response: any) => {
        const userRole = response.role; // Assuming 'role' is returned as 'COACH', 'ENTREPRENEUR', or 'INVESTOR'
        this.setMenuBasedOnRole(userRole);
      },
      error: (error) => {
        console.error('Failed to fetch user profile:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to fetch user role',
        });
        this.router.navigate(['/signin']);
      },
    });
  }

  // Common Profile Menu Item
  private getProfileMenuItem(): MenuItem {
    return [
      {
        label: 'Profile',
        items: [
          {
            label: 'Profile',
            icon: 'pi pi-fw pi-user',
            routerLink: ['/profile']
          }
        ]
      }
    ];
  }

  // Set the menu based on the user's role and append Profile
  private setMenuBasedOnRole(role: string): void {
    let roleMenu: MenuItem[] = [];
    switch (role) {
      case 'ENTREPRENEUR':
        roleMenu = this.getEntrepreneurMenu();
        break;
      case 'COACH':
        roleMenu = this.getCoachMenu();
        break;
      case 'INVESTOR':
        roleMenu = this.getInvestorMenu();
        break;
      default:
        console.warn('Unknown role:', role);
        roleMenu = []; // Empty menu or fallback
        this.messageService.add({
          severity: 'warn',
          summary: 'Warning',
          detail: 'Unknown user role detected',
        });
    }
    // Append Profile item to all menus
    this.model = [...roleMenu, this.getProfileMenuItem()];
  }

  // Entrepreneur Menu
  private getEntrepreneurMenu(): MenuItem[] {
    return [
      {
        label: 'Home',
        items: [{ label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/dashboard'] }],
      },
      {
        label: 'Projet',
        items: [
          { label: 'Projet', icon: 'pi pi-fw pi-home', routerLink: ['/addprojet'] },
          { label: 'Projets', icon: 'pi pi-fw pi-home', routerLink: ['/GetProjet'] },
        ],
      },
      {
        label: 'Accompagnement',
        items: [
          { label: 'Accompagnement', icon: 'pi pi-fw pi-home', routerLink: ['/phases'] },
        ],
      },
      {
        label: 'Rendez-vous',
        items: [
          { label: 'Rendez-vous', icon: 'pi pi-fw pi-calendar', routerLink: ['/appointments'] },
          { label: 'Meeting', icon: 'pi pi-fw pi-calendar', routerLink: ['/meeting'] },
          { label: 'My Meetings List', icon: 'pi pi-fw pi-calendar', routerLink: ['/meetinglist'] },
        ],
      },
      {
        label: 'Mes Documents',
        items: [
          { label: 'Docs', icon: 'pi pi-fw pi-file', routerLink: ['/gestion-docs'] },
        ],
      },
      {
        label: 'Mes Reclamations',
        items: [
          { label: 'Reclamation', icon: 'pi pi-fw pi-file', routerLink: ['/messagerie-reclamation'] },
        ],
      },
      {
        label: 'Startup View',
        items: [{ label: 'My Requests', icon: 'pi pi-fw pi-briefcase', routerLink: ['/startup/v1'] }],
      },
    ];
  }

  // Coach Menu
  private getCoachMenu(): MenuItem[] {
    return [
      {
        label: 'Home',
        items: [{ label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/dashboard'] }],
      },
      {
        label: 'Rendez-vous',
        items: [
          { label: 'Disponibilités', icon: 'pi pi-fw pi-calendar', routerLink: ['/appointments/received'] },
        ],
      },
      {
        label: 'Accompagnements',
        items: [
          { label: 'Accompagnement', icon: 'pi pi-fw pi-home', routerLink: ['/phases'] },
        ],
      },
      {
        label: 'Mes Projets',
        items: [
          { label: 'Projets', icon: 'pi pi-fw pi-home', routerLink: ['/GetProjet'] },
        ],
      },
      {
        label: 'Les Documents',
        items: [
          { label: 'Docs', icon: 'pi pi-fw pi-file', routerLink: ['/gestion-docs'] },
        ],
      },
      {
        label: 'Mes Reclamations',
        items: [
          { label: 'Reclamation', icon: 'pi pi-fw pi-file', routerLink: ['/messagerie-reclamation'] },
        ],
      },
    ];
  }

  // Investor Menu
  private getInvestorMenu(): MenuItem[] {
    return [
      {
        label: 'Home',
        items: [{ label: 'Dashboard', icon: 'pi pi-fw pi-home', routerLink: ['/dashboard'] }],
      },
      {
        label: 'Marketplace',
        items: [{ label: 'Marketplace', icon: 'pi pi-fw pi-home', routerLink: ['/marketplace/v1'] }],
      },
      {
        label: 'Startups',
        items: [{ label: 'My Startups', icon: 'pi pi-fw pi-briefcase', routerLink: ['/investor/v1'] }],
      },
      {
        label: 'Les Documents',
        items: [
          { label: 'Docs', icon: 'pi pi-fw pi-file', routerLink: ['/gestion-docs'] },
        ],
      },
      {
        label: 'Mes Reclamations',
        items: [
          { label: 'Reclamation', icon: 'pi pi-fw pi-file', routerLink: ['/messagerie-reclamation'] },
        ],
      },
    ];
  }
}