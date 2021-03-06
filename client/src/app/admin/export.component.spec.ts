import {ExportComponent} from "./export.component";
import {ComponentFixture, TestBed, async} from "@angular/core/testing";
import {Observable} from "rxjs";
import {FormsModule} from "@angular/forms";
import {RouterTestingModule} from "@angular/router/testing";
import {NavbarComponent} from "../navbar/navbar.component";
import {AdminService} from "./admin.service";
import {Router} from "@angular/router";
import {LoginComponent} from "./login.component";
describe("Directive: Export", () => {
    let exportComponent: ExportComponent;
    let fixture: ComponentFixture<ExportComponent>;
    let hasCookie: boolean = true;
    let sss: string  = "/admin";
    let TRUEE : Promise<boolean>;
    let promise: Promise<boolean> = new Promise((resolve, reject) => resolve(true));


    let adminServiceStub: {
        getUploadIds: () => Observable<string[]>
        getLiveUploadId: () => Observable<string>
        checkHasCookie: () => Observable<boolean>
    };

    beforeEach(() => {
        adminServiceStub = {
            getUploadIds: () => {
                return Observable.of([
                    "this is an uploadId",
                    "this is also an upload id",
                    "this is your mom"
                ])},

            getLiveUploadId: () => {
                return Observable.of(
                    "this is a live upload id"
                )
            },

            checkHasCookie: () => {
                return Observable.of(true)
            }
        };


        TestBed.configureTestingModule({
            imports: [FormsModule, RouterTestingModule.withRoutes([])],
            declarations: [ExportComponent, NavbarComponent, LoginComponent],
            providers: [{provide: AdminService, useValue: adminServiceStub}]
        }).compileComponents();
    });

    beforeEach(
        async(() => {
            TestBed.compileComponents().then(() => {
                fixture = TestBed.createComponent(ExportComponent);
                exportComponent = fixture.componentInstance;
                fixture.detectChanges();
            });
        }));


    it("There is a liveUploadId", () => {
        expect(exportComponent.liveUploadId).toEqual("this is a live upload id");
    });

    it ("There are uploadIds", () => {
        expect(exportComponent.uploadIds[0]).toEqual("this is an uploadId");
    });
});
