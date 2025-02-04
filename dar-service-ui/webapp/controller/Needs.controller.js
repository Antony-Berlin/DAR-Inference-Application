sap.ui.define([
  "sap/ui/core/mvc/Controller",
  "sap/ui/model/json/JSONModel",
  "sap/m/MessageToast",
  "sap/m/MessageBox",
  "sap/m/Label",
  "sap/m/Select",
  "sap/ui/core/Item",
  "sap/ui/layout/GridData"
], function (Controller, JSONModel, MessageToast, MessageBox, Label, Select, Item, GridData) {
  "use strict";

  return Controller.extend("ui5.walkthrough.controller.Needs", {
    onInit: function () {
      var oModel = new JSONModel();
      this.getView().setModel(oModel);

      // Load needs.json and create form elements
      var that = this;
      $.getJSON("needs.json", function(data) {
        that.createFormElements(data.product);
      });
    },

    createFormElements: function(products) {
      var oVBox = this.getView().byId("formContainer");
      var oHBox;
      products.forEach(function(product, index) {
        if (index % 3 === 0) {
          oHBox = new sap.m.HBox({ justifyContent: "SpaceBetween" });
          oVBox.addItem(oHBox);
        }
        var oLabel = new Label({
          text: product.need_Attribute,
          labelFor: "select" + index
        });
        var oSelect = new Select({
          id: "select" + index,
          width: "200px",
          items: [
            new Item({ key: "", text: "" })
          ].concat(product.characteristicvalues.map(function(value) {
            return new Item({
              key: value,
              text: value
            });
          }))
        });
        var oVBoxItem = new sap.m.VBox({
          items: [oLabel, oSelect]
        });
        oVBoxItem.setWidth("33%");
        oHBox.addItem(oVBoxItem);
      });
    },

    onShowProduct: function() {
      var oModel = this.getOwnerComponent().getModel("DarInferenceModel"),
          oListBinding = oModel.bindList("/Root");
      var oPayload = this.getpayload();

      this.oContext = oListBinding.create(oPayload, true);
      var that = this;
      this.oContext.created().then(
        function (data) {
          try {
            var resultData = JSON.parse(that.oContext.getValue().Response.responseText);
            console.log(resultData);
            MessageToast.show("Success");
            var oNavContainer = that.getOwnerComponent().getRootControl().byId("navContainer");
            var oResultView = oNavContainer.getPages().find(page => page.getId().includes("resultView"));
            oResultView.getController().setResponseData(resultData);
            oNavContainer.to(oResultView);
          } catch (e) {
            MessageBox.error("Incomplete response returned from backend."+e);
          }
        }
      ).catch(function(oError) {
        MessageToast.show("Error: " + oError.message);
        that.oContext.delete("$direct");
      });
    },

    getpayload: function() {
      var inputs = this.getView().findAggregatedObjects(true, function(oControl) {
        return oControl.isA("sap.m.Select");
      });

      var features = inputs.map(function(select, index) {
        return {
          name: select.getParent().getItems()[0].getText(),
          value: select.getSelectedKey()
        };
      });

      return {
        topN: 2,
        objects: [
          {
            features: features
          }
        ]
      };
    }
  });
});