import {render, kalturaSaasEndpoint} from "@openequella/react-kaltura-simpleuploader";

/**
 * Provide the access to the TS Upload control
 * @param id The HTML element where the Upload control to be rendered.
 * @param ks Kaltura Session key.
 * @param partnerId Kaltura partner ID.
 */
export const setupKalturaUpload = (id: string, ks: string, partnerId: number) => {
  const kalturaUploadControl = document.getElementById(id);
  if(!kalturaUploadControl) {
    throw new Error("Failed to load Kaltura Upload Control")
  }
  render(kalturaUploadControl , kalturaSaasEndpoint, ks, partnerId);
}
