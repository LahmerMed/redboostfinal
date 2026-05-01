import { Routes } from '@angular/router';
import { AppLayout } from './app/layout/component/app.layout';
import { Dashboard } from './app/pages/dashboard/dashboard';
import { Documentation } from './app/pages/documentation/documentation';
import { Landing } from './app/pages/landing/landing';
import { GestionDocsComponent } from './app/pages/gestion-docs/gestion-docs.component';
import { UserLibraryComponent } from './app/pages/gestion-docs/gestion-folder/folder-details/user-library/user-library.component'; 
import { Notfound } from './app/pages/notfound/notfound';
import { GestionReclamationComponent } from './app/gestion-reclamation/gestion-reclamation.component';
import { MessagerieReclamationComponent } from './app/messagerie-reclamation/messagerie-reclamation.component';
import { PhaseListComponent } from './app/phases/phase-list/phase-list.component';
import { KanbanBoardComponent } from './app/kanban-board/kanban-board.component';
import { SigninComponent } from './app/pages/auth/signin/signin.component'; 
import { SignUpComponent } from './app/pages/auth/signup/signup.component';
import { ConfirmEmailComponent } from './app/pages/auth/confirm-email/confirm-email.component';
import { MeetingComponent } from './app/pages/streaming/meeting/meeting.component'; 
import { MeetingListComponent } from './app/pages/streaming/meetinglist/meeting-list.component';
import { JitsiMeetingComponent } from './app/pages/streaming/jitsi-meeting/jitsi-meeting.component';
import { MarketplaceComponent } from './app/pages/marketplace/marketplace';
import { AppointmentsReceivedComponent } from './app/pages/Coach/AppointmentsReceived/appointments-received.component';
import { AddProjetComponent } from './app/pages/Projet/addprojet/addprojet.component';
import { AfficheProjetComponent } from './app/pages/Projet/affiche-projet/affiche-projet.component';
import { DetailsProjetComponent } from './app/pages/Projet/details-projet/details-projet.component';
import { Crud } from './app/pages/crud/crud';
import { Empty } from './app/pages/empty/empty';
import { SubFolderComponent } from './app/pages/gestion-docs/gestion-folder/sub-folder/sub-folder.component';  // Import SubFolderComponent
import {UserProfileComponent} from './app/pages/profile/profile.component'; // Import UserProfileComponent
export const pagesRoutes: Routes = [
    { path: 'addprojet', component: AddProjetComponent },
    { path: 'GetProjet', component: AfficheProjetComponent },
    { path: 'details-projet/:id', component: DetailsProjetComponent },
    { path: 'crud', component: Crud },
    { path: 'empty', component: Empty },
    { path: 'gestion-reclamation', component: GestionReclamationComponent },
    { path: 'marketplace', component: MarketplaceComponent },
    { path: 'meeting', component: MeetingComponent },
    { path: 'meetinglist', component: MeetingListComponent },
    { path: 'appointments/received', component: AppointmentsReceivedComponent },
    {path: 'profile', component: UserProfileComponent}, // Add route for UserProfileComponent

]; 

export const appRoutes: Routes = [
    { path: '', component: Landing },
    { path: 'signin', component: SigninComponent },
    { path: 'signup', component: SignUpComponent },
    { path: 'confirm-email', component: ConfirmEmailComponent },
    {
        path: '',
        component: AppLayout,
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' }, // Default route
            { path: 'dashboard', component: Dashboard },
            { path: 'documentation', component: Documentation },
            { path: 'gestion-docs', component: GestionDocsComponent },
            {path: 'profile', component: UserProfileComponent}, // Add route for UserProfileComponent
            { path: 'sub-folder/:folderName/:folderMetadataId/:categoryName/:subFolderName', component: SubFolderComponent },  // Updated route
            { path: 'gestion-folder/folder-details/user-library/:folderName/:folderMetadataId', component: UserLibraryComponent }, // Corrected route
            { path: 'messagerie-reclamation', component: MessagerieReclamationComponent },
            {
                path: 'phases',
                children: [
                    { path: '', component: PhaseListComponent },
                    { path: ':phaseId', component: KanbanBoardComponent }
                ]
            },
            { path: 'marketplace/v1', loadChildren: () => import('./app/pages/marketplace/marketplace.routes') },
            { path: 'investor/v1', loadChildren: () => import('./app/pages/startup/investorStartups/investor.routes') },
            { path: 'startup/v1', loadChildren: () => import('./app/pages/startup/StartupsRequest/startup.routes') },
            { path: 'startup/:id', loadChildren: () => import('./app/pages/marketplace/StartupDetails/startup.routes') },
            {
                path: 'appointments',
                loadChildren: () => import('./app/pages/appointments/appointments.module').then(m => m.AppointmentsModule)
            },
            ...pagesRoutes
        ]
    },
    { path: 'landing', component: Landing },
    { path: 'auth', loadChildren: () => import('./app/pages/auth/auth.routes') },
    { path: 'notfound', component: Notfound },
    { path: 'jitsi-meeting', component: JitsiMeetingComponent },
    { path: '**', redirectTo: 'notfound' }
];