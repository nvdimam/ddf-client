/**
 * Created by dimam on 04/11/2016.
 */

(function($) {

    // shorten references to variables. this is better for uglification var kendo = window.kendo,
    ui = kendo.ui,
        Widget = ui.Widget;

    var MyWidget = Widget.extend({

        init: function (element, options) {

            // base call to widget initialization
            Widget.fn.init.call(this, element, options);

        },

        options: {
            // the name is what it will appear as off the kendo namespace(i.e. kendo.ui.MyWidget).
            // The jQuery plugin would be jQuery.fn.kendoMyWidget.
            name: "MyWidget",
            // other options go here

        }

    },1);

    ui.plugin(MyWidget);

})(jQuery);