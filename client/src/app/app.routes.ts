// Imports
import { ModuleWithProviders }  from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminComponent } from "./admin/admin.component";
import { ExportComponent } from "./admin/export.component";
import { ImportComponent } from "./admin/import.component";
import { GardenComponent } from "./garden/garden-component";
import { SearchComponent } from "./search/search.component";
import {GraphComponent} from "./admin/google-charts.component";
import {PasswordChangeComponent} from "./admin/passwordChange.component";

// Route Configuration
export const routes: Routes = [
    { path: '', component: GardenComponent },
    { path: 'admin', component: AdminComponent},
    { path: 'admin/exportData', component: ExportComponent},
    { path: 'admin/importData', component: ImportComponent},
    { path: 'search', component: SearchComponent},
    { path: 'admin/charts', component: GraphComponent},
    {path: 'admin/newPass', component: PasswordChangeComponent},
    { path: '**', component: GardenComponent}

];

export const routing: ModuleWithProviders = RouterModule.forRoot(routes);