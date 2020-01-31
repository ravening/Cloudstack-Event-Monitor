function loadComments () {
    console.log('coming jhere');
    this.source = null;

    this.start = function () {

        var commentTable = document.getElementById("alerts");

        console.log('calling the api')
        this.source = new EventSource("/api/stream");

        this.source.addEventListener("message", function (event) {

            console.log('got the input');
            // These events are JSON, so parsing and DOM fiddling are needed
            var comment = JSON.parse(event.data);

            console.log(comment);
            var row = commentTable.getElementsByTagName("tbody")[0].insertRow(0);
            var cell0 = row.insertCell(0);
            var cell1 = row.insertCell(1);
            var cell2 = row.insertCell(2);

            cell0.className = "text";
            cell0.innerHTML = comment.name;

            cell1.className = "text";
            cell1.innerHTML = comment.sent;

            cell2.className = "text";
            cell2.innerHTML = comment.description;

        });

        this.source.onerror = function () {
            this.close();
        };

    };

    this.stop = function() {
        this.source.close();
    }

}

comment = new loadComments();

/*
 * Register callbacks for starting and stopping the SSE controller.
 */
window.onload = function() {
    comment.start();
};
window.onbeforeunload = function() {
    comment.stop();
}