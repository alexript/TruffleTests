const StdLib = {
    __getJsDate: function (javaDate) {
        let d = new Date(javaDate.getTime());
        return d;
    }
};