// Imports
import { ModuleWithProviders }  from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {PlantListComponent} from "./plants/plant-list.component";
import {PlantComponent} from "./plants/plant.component";
import {AdminComponent} from "./admin/admin.component";
import {ExportComponent} from "./admin/export.component";
// Route Configuration
export const routes: Routes = [
    { path: '', component: PlantListComponent },
    { path: 'plants/:plantID', component: PlantComponent },
    { path: 'admin', component: AdminComponent},
    { path: 'admin/exportData', component: ExportComponent}
];

export const routing: ModuleWithProviders = RouterModule.forRoot(routes);