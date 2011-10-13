var UserInterface = {
  disableFileSelection: function() {
    $("#fileToUpload").attr("disabled", "disabled");
  },
  showStatusBar: function() {
    $("#progress").css("visibility", "visible");
  },
  hideStatusBar: function() {
    $("#progress").css("visibility", "hidden");
  },
  updateStatusTo: function(percentage) {
    $("#status").html(percentage + "%");
  },
  enableDetailsButton: function() {
    $("#saveDetails").removeAttr("disabled");
  },
  showFilePath: function(filePath) {
    $("#filePath").html("<a href=\"" + filePath + "\">File available here.</a>");
  },
  showErrorBox: function() {
    $("#errorBox").show();
  }
};

var Uploader = {
  uploadId: function() {
    return $("#uid").val();
  },
  statusUrl: function () {
    return "/status";
  },
  performUpload: function() {
    var ui = UserInterface;
    var completed = false;
    var refreshTimeout;

    var isCompleted = function(status) {
      return status.completed >= 100 && status.filePath != undefined;
    };

    var cancelTimeout = function() {
      clearTimeout(refreshTimeout);
    };

    var refreshProgress = function() {
      $.ajax ({
        url: Uploader.statusUrl(),
        data: { "uid": Uploader.uploadId() },
        success: function(status) {
          percentage = status.completed;

          ui.updateStatusTo(percentage);
          completed = isCompleted(status);

          if (completed) {
            cancelTimeout();
            ui.showFilePath(status.filePath);
            ui.enableDetailsButton();
          }
        },
        error: function(xhr, message) {
          cancelTimeout();
          ui.hideStatusBar();
          ui.showErrorBox();
        }
      });    
    }; 

    var checkProgress = function() {
      if (!completed) {    
        refreshTimeout = setTimeout(function() {
          refreshProgress();
          checkProgress();
        }, 1000);
      }
    };

    $("#uploadForm").submit();
    ui.disableFileSelection();
    ui.showStatusBar();

    checkProgress();
  }
};

$(document).ready(function() {
  $("#fileToUpload").change(Uploader.performUpload);
});
