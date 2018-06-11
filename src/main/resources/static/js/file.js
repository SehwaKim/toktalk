$(document).ready(function(){
    var objDragAndDrop = $(".dragAndDropDiv");

    $(document).on("dragenter",".dragAndDropDiv",function(e){
        e.stopPropagation();
        e.preventDefault();
        $(this).css('border', '2px solid #0B85A1');
    });
    $(document).on("dragover",".dragAndDropDiv",function(e){
        e.stopPropagation();
        e.preventDefault();
    });
    $(document).on("drop",".dragAndDropDiv",function(e){

        $(this).css('border', '2px dotted #0B85A1');
        e.preventDefault();
        var files = e.originalEvent.dataTransfer.files;

        handleFileUpload(files,objDragAndDrop);
        // uploadFileUrl(files,objDragAndDrop);
    });

    $(document).on('dragenter', function (e){
        e.stopPropagation();
        e.preventDefault();
    });
    $(document).on('dragover', function (e){
        e.stopPropagation();
        e.preventDefault();
        objDragAndDrop.css('border', '2px dotted #0B85A1');
    });
    $(document).on('drop', function (e){
        e.stopPropagation();
        e.preventDefault();
    });

    function handleFileUpload(files,obj)
    {
        for (var i = 0; i < files.length; i++)
        {
            var fd = new FormData();
            fd.append('file', files[i]);
            fd.append('channelId',current);

            var status = new createStatusbar(obj); //Using this we can set progress.
            status.setFileNameSize(files[i].name,files[i].size);
            sendFileToServer(fd,status);
        }
    }

    //ajax 다운로드
    jQuery.download = function(url,data,method)
    {
        if(url&&data){
            data = typeof data == 'string' ? data : jQuery.param(data);
            var inputs = '';
            jQuery.each(data.split('&'), function(){
                var pair = this.split('=');
                inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />';
            });
        }
    }

    var rowCount=0;
    function createStatusbar(obj){

        rowCount++;
        var row="odd";
        if(rowCount %2 ==0) row ="even";
        this.statusbar = $("<div class='statusbar "+row+"'></div>");
        this.filename = $("<div class='filename'></div>").appendTo(this.statusbar);
        this.size = $("<div class='filesize'></div>").appendTo(this.statusbar);
        this.progressBar = $("<div class='progressBar'><div></div></div>").appendTo(this.statusbar);
        this.abort = $("<div class='abort'>중지</div>").appendTo(this.statusbar);

        obj.after(this.statusbar);

        this.setFileNameSize = function(name,size){
            var sizeStr="";
            var sizeKB = size/1024;
            if(parseInt(sizeKB) > 1024){
                var sizeMB = sizeKB/1024;
                sizeStr = sizeMB.toFixed(2)+" MB";
            }else{
                sizeStr = sizeKB.toFixed(2)+" KB";
            }

            this.filename.html(name);
            this.size.html(sizeStr);
        }

        this.setProgress = function(progress){
            var progressBarWidth =progress*this.progressBar.width()/ 100;
            this.progressBar.find('div').animate({ width: progressBarWidth }, 10).html(progress + "% ");
            if(parseInt(progress) >= 100)
            {
                this.abort.hide();
            }
        }

        this.setAbort = function(jqxhr){
            var sb = this.statusbar;
            this.abort.click(function()
            {
                jqxhr.abort();
                sb.hide();
            });
        }
    }

    function sendFileToServer(formData,status)
    {
        var uploadURL = "/api/messages/fileUpload/post"; //Upload URL
        var extraData ={}; //Extra Data.
        var jqXHR=$.ajax({
            xhr: function() {
                var xhrobj = $.ajaxSettings.xhr();
                if (xhrobj.upload) {
                    xhrobj.upload.addEventListener('progress', function(event) {
                        var percent = 0;
                        var position = event.loaded || event.position;
                        var total = event.total;
                        if (event.lengthComputable) {
                            percent = Math.ceil(position / total * 100);
                        }
                        //Set progress
                        status.setProgress(percent);
                    }, false);
                }
                return xhrobj;
            },
            url: uploadURL,
            type: "POST",
            contentType:false,
            processData: false,
            cache: false,
            data: formData,
            success: function(data){
                status.setProgress(100);
                //$("#status1").append("File upload Done<br>");
            }
        });
        status.setAbort(jqXHR);
    }
});