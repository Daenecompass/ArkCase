/**
 * Acm.Dialog is used to display Dialog/confirm/error messages to the user
 *
 * Current implementation is to use bootbox
 *
 * @author jwu
 */
Acm.Dialog = {
    initialize : function() {
    }

    ,DEFAULT_NO_TITLE:    undefined
    ,DEFAULT_NO_CALLBACK: undefined

    //
    // callback example:
    // function() {
    //    alert("callback");
    // }
    //
    ,info: function(msg, callback, title){
        var opt = {
            message: msg
        }
        if(Acm.isNotEmpty(title)) {
            opt.title = title;
        } else {
            opt.title = "Info";
        }
        if (Acm.isNotEmpty(callback)) {
            opt.callback = callback;
        }

        bootbox.alert(opt);
    }
    ,alert: function(msg, callback, title){
        if(Acm.isEmpty(title)) {
            title = "Alert";
        }
        this.info(msg, callback, title);
    }
    ,error: function(msg, callback, title){
        if(Acm.isEmpty(title)) {
            title = "Error";
        }
        this.info(msg, callback, title);
    }

    //
    // callback example:
    // function(result) {
    //    alert("callback result:" + result);
    // }
    //
    ,confirm: function(msg, callback, title){
        if (Acm.isEmpty(callback)) {
            console.log("Confirm dialog needs callback");
            return;
        }

        bootbox.confirm(msg, callback);
    }

    //
    // callback example:
    // function(result) {
    //    if (null === result) {
    //        alert("Prompt dismissed");
    //    } else {
    //        alert("Prompt result:" + result);
    //    }
    // }
    //
    ,prompt: function(msg, callback, title){
        if (Acm.isEmpty(callback)) {
            console.log("Prompt dialog needs callback");
            return;
        }

        bootbox.prompt(msg, callback);
    }

}