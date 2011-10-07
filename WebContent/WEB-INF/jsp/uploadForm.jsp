<!DOCTYPE html>
<html>
  <head>
    <title>Cloud Upload</title>
  </head>
  <body>
    <h1>Upload your music!</h1>
    <div id="main">
      <div id="fileSelection">
        <form id="uploadForm" action="${pageContext.request.contextPath}/upload" target="uploadContainer" method="post" enctype="multipart/form-data">
          <input id="uid" type="hidden" name="uid" value="${uid}" />
          <input id="fileToUpload" type="file" name="fileToUpload" />
        </form>

        <iframe id="uploadContainer" name="uploadContainer" style="display: none; visibility: hidden; border: 0;"></iframe>
      </div>

      <div id="progress" style="display: none;">
        Uploaded: <span id="status">0%</span>
      </div>

      <div id="dataInput">
        <form id="detailsForm" action="#" method="post">
          <textarea name="details"></textarea>
          <input id="saveDetails" type="submit" value="Save" disabled />
        </form>
      </div>
    </div>

    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/cloud-upload.js" type="text/javascript"></script>
  </body>
</html>
