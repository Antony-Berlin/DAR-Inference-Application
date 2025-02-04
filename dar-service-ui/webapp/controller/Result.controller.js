// filepath: /Users/I528959/ipr/Prototype/dar-service-ui/webapp/controller/Result.controller.js
sap.ui.define([
  "sap/ui/core/mvc/Controller"
], function (Controller) {
  "use strict";

  return Controller.extend("ui5.walkthrough.controller.Result", {
    onInit: function () {
    },

    setResponseData: function (data) {
      var oModel = new sap.ui.model.json.JSONModel(data);
      this.getView().setModel(oModel, "responseModel");

      var oText = this.getView().byId("responseText");
      oText.setText(JSON.stringify(data, null, 2));
    }
  });
});