import {setupKalturaUpload} from "./src/UploadControl";

/**
 * This plain Javascript file is used to make the Kaltura TS Upload control
 * accessible from oEQ server side. It basically adds the setup function to
 * global object `window`.
 */

window.KalturaUploadaControl = setupKalturaUpload
