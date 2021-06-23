import {kalturaSaasEndpoint, render} from "@openequella/react-kaltura-simpleuploader";
import {KalturaMediaEntry} from "kaltura-typescript-client/api/types";
import "@openequella/react-kaltura-simpleuploader/dist/styles.css"

/**
 * Provide the access to the TS Upload control
 * @param id The HTML element where the Upload control to be rendered.
 * @param ks Kaltura Session key.
 * @param partnerId Kaltura partner ID.
 * @param callback Server-provided callback which should be called when an upload in completed.
 */
export const setupKalturaUpload = (id: string, ks: string, partnerId: number, callback: (entries: KalturaMediaEntry[])=> void) => {
  const kalturaUploadControl = document.getElementById(id);
  if(!kalturaUploadControl) {
    throw new Error("Failed to load Kaltura Upload Control")
  }
  render(kalturaUploadControl , kalturaSaasEndpoint, ks, partnerId, callback);
}
