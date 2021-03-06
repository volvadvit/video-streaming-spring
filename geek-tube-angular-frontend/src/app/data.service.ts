import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {VideoMetadata} from "./video-metadata";

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient) {
  }

    /**
     Method with mock previews
    */

//   public findAllPreviews() {
//     return new Promise<VideoMetadata[]>((resolve, reject) =>
//     {
//       resolve([
//         new VideoMetadata(1, 'First video', 'video/mp4', 'https://loremflickr.com/213/106', ''),
//         new VideoMetadata(2, 'Second video', 'video/mp4', 'https://loremflickr.com/213/105', ''),
//         new VideoMetadata(3, 'Third video', 'video/mp4', 'https://loremflickr.com/213/101', ''),
//         new VideoMetadata(4, 'Fourth video', 'video/mp4', 'https://loremflickr.com/213/103', ''),
//         new VideoMetadata(5, 'Fifth video', 'video/mp4', 'https://loremflickr.com/213/104', ''),
//       ])
//     })
//   }

  public findAllPreviews() {
    return this.http.get<VideoMetadata[]>('/api/v1/video/all').toPromise()
  }

  public findById(id: number) {
    return this.http.get<VideoMetadata>('/api/v1/video/' + id).toPromise()
  }

  public uploadNewVideo(formData: FormData) {
    return this.http.post('/api/v1/video/upload', formData).toPromise()
  }
}
