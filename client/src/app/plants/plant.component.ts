
import { PlantListService } from "./plant-list.service";
import { Plant } from "./plant";
import { Component, OnInit, Input, Output, OnChanges, EventEmitter, trigger, state, style, animate, transition } from '@angular/core';

@Component({
    selector: 'plant-component',
    templateUrl: 'plant.component.html'
})
export class PlantComponent implements OnInit {
    @Input() plant : Plant;
    @Input() showDialog : boolean;
    //public plant: Plant = null;
    private id: string;

    constructor(private plantListService: PlantListService) {
        // this.plants = this.plantListService.getPlants();
    }

    /*
    private subscribeToServiceForId() {
        if (this.id) {
            this.plantListService.getPlantById(this.id).subscribe(
                plant => this.plant = plant,
                err => {
                    console.log(err);
                }
            );
        }
    }

    setId(id: string) {
        this.id = id;
        this.subscribeToServiceForId();
    }*/

    ngOnInit(): void {
        //this.subscribeToServiceForId();
        // console.log(this.plant.cultivar);
    }
}
