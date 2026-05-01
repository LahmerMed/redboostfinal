import { Component } from '@angular/core';
import { StyleClassModule } from 'primeng/styleclass';
import { Router, RouterModule } from '@angular/router';
import { RippleModule } from 'primeng/ripple';
import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'topbar-widget',
    standalone: true,
    imports: [CommonModule, RouterModule, StyleClassModule, ButtonModule, RippleModule],
    template: `
        <div class="fixed top-0 left-0 w-full z-50 backdrop-blur-md bg-opacity-90 bg-surface-0 dark:bg-surface-900/90 shadow-lg">
            <!-- Top Contact and Social Media Section (Hidden on Mobile) -->
            <div class="hidden sm:flex items-center justify-between px-8 py-3 bg-gray-100">
                <div class="flex items-center gap-6">
                    <span class="text-gray-700 text-base hover:text-[#DB1E37] transition-colors duration-300">
                        <i class="pi pi-phone mr-2"></i>+216 71 793 125
                    </span>
                    <span class="text-gray-700 text-base hover:text-[#DB1E37] transition-colors duration-300">
                        <i class="pi pi-envelope mr-2"></i>hello&#64;redstart.tn
                    </span>
                </div>
                <div class="flex items-center gap-6">
                    <span class="text-gray-700 text-base">Find us on:</span>
                    <a href="#" class="text-[#0A4955] hover:text-[#DB1E37] transition-colors duration-300">
                        <i class="pi pi-facebook text-2xl hover:scale-110 transition-transform duration-300"></i>
                    </a>
                    <a href="#" class="text-[#0A4955] hover:text-[#DB1E37] transition-colors duration-300">
                        <i class="pi pi-instagram text-2xl hover:scale-110 transition-transform duration-300"></i>
                    </a>
                    <a href="#" class="text-[#0A4955] hover:text-[#DB1E37] transition-colors duration-300">
                        <i class="pi pi-linkedin text-2xl hover:scale-110 transition-transform duration-300"></i>
                    </a>
                </div>
            </div>

            <!-- Main Navigation -->
            <div class="flex items-center justify-between px-4 sm:px-8 py-4">
                <!-- Logo -->
                <a class="flex items-center" href="#">
                    <img src="assets/images/logo_redboost.png" alt="RedBoost Logo" class="h-8 mr-2" />
                </a>

                <!-- Hamburger Menu for Mobile -->
                <div class="flex items-center gap-4 sm:hidden">
                    <button pButton pRipple label="Se connecter" routerLink="/signin" [rounded]="true" [text]="true" class="custom-button-login transition-colors duration-300 transform hover:scale-105"></button>
                    <button pButton pRipple label="S'inscrire" routerLink="/signup" [rounded]="true" [text]="false" class="custom-button-register transition-colors duration-300 transform hover:scale-105"></button>
                    <button class="p-2 text-surface-900 dark:text-surface-0" (click)="toggleMenu()">
                        <i class="pi pi-bars text-2xl"></i>
                    </button>
                </div>

                <!-- Navigation Links (Hidden on Mobile) -->
                <ul class="list-none p-0 m-0 hidden sm:flex items-center gap-6 sm:gap-10 text-center">
                    <li *ngFor="let item of menuItems">
                        <a (click)="navigateTo(item.route, item.fragment)" pRipple class="text-surface-900 dark:text-surface-0 font-semibold text-lg sm:text-xl relative group">
                            <span class="relative">
                                {{ item.label }}
                                <span class="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-primary-500 to-secondary-500 transition-all duration-300 group-hover:w-full"></span>
                            </span>
                        </a>
                    </li>
                </ul>

                <!-- Login and Register Buttons (Hidden on Mobile) -->
                <div class="hidden sm:flex gap-4">
                    <button pButton pRipple label="Se connecter" routerLink="/signin" [rounded]="true" [text]="true" class="custom-button-login transition-colors duration-300 transform hover:scale-105"></button>
                    <button pButton pRipple label="S'inscrire" routerLink="/signup" [rounded]="true" [text]="false" class="custom-button-register transition-colors duration-300 transform hover:scale-105"></button>
                </div>
            </div>

            <!-- Mobile Menu (Hidden by Default) -->
            <div class="sm:hidden" [ngClass]="{'hidden': !isMenuOpen}">
                <ul class="list-none p-0 m-0 flex flex-col items-center gap-4 py-4 bg-surface-0 dark:bg-surface-900">
                    <li *ngFor="let item of menuItems">
                        <a (click)="navigateTo(item.route, item.fragment)" pRipple class="text-surface-900 dark:text-surface-0 font-semibold text-lg relative group">
                            <span class="relative">
                                {{ item.label }}
                                <span class="absolute bottom-0 left-0 w-0 h-0.5 bg-gradient-to-r from-primary-500 to-secondary-500 transition-all duration-300 group-hover:w-full"></span>
                            </span>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    `,
    styles: [`
        .shadow-lg {
            box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.2);
        }

        .backdrop-blur-md {
            backdrop-filter: blur(10px);
        }

        .bg-opacity-90 {
            background-opacity: 0.9;
        }

        .bg-gradient-to-r {
            background-image: linear-gradient(to right, var(--tw-gradient-from), var(--tw-gradient-to));
        }

        .from-primary-500 {
            --tw-gradient-from: #DB1E37;
        }

        .to-secondary-500 {
            --tw-gradient-to: #FF6B6B;
        }

        .group:hover .group-hover-w-full {
            width: 100%;
        }

        .hover-scale-105:hover {
            transform: scale(1.05);
        }

        .hover-bg-primary-500-10:hover {
            background-color: rgba(219, 30, 55, 0.1);
        }

        .hover-bg-gradient-to-r:hover {
            background-image: linear-gradient(to right, #DB1E37, #FF6B6B);
        }

        /* Custom Button Styles */
        .custom-button-login.p-button {
            background-color: white !important;
            color: #0A4955 !important;
            border: 1px solid #0A4955 !important;
            border-radius: 10px !important;
            font-weight: bold;
            padding: 12px 24px;
            transition: transform 0.2s ease-in-out, background-color 0.2s ease-in-out, color 0.2s ease-in-out, border-color 0.2s ease-in-out !important;
        }

        .custom-button-login.p-button:hover {
            background-color: #0A4955 !important;
            color: #fff !important;
            border: 1px solid #76c8da !important;
            transform: scale(1.05);
        }

        .custom-button-register.p-button:enabled:hover {
            background: linear-gradient(to right, #0A4955, #DB1E37);
            color: white;
            transform: scale(1.05);
        }

        .custom-button-register {
            background: linear-gradient(to right, #DB1E37, #0A4955);
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 10px;
            transition: background 0.3s ease;
            cursor: pointer;
            font-size: 16px;
        }
    `]
})
export class TopbarWidget {
    menuItems = [
        { label: 'About Us', route: '/about' },
        { label: 'Our Services', route: '/landing', fragment: 'Our Services' },
        { label: 'Resources', route: '/landing', fragment: 'Resources' },
        { label: 'Marketplace', route: '/landing', fragment: 'Marketplace' },
        { label: 'Contact', route: '/contact' }
    ];

    isMenuOpen = false;

    constructor(public router: Router) {}

    navigateTo(route: string | undefined, fragment: string | undefined = undefined) {
        if (route) {
            if (fragment) {
                this.router.navigate([route], { fragment });
            } else {
                this.router.navigate([route]);
            }
        }
        this.isMenuOpen = false; // Close menu after navigation
    }

    toggleMenu() {
        this.isMenuOpen = !this.isMenuOpen;
    }
}
