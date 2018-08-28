# sb-mybook-angular

This application uses file upload with formdata and multipart/form-data request with
Content-Type: multipart/form-data.

To parse formdata fields on Spring Boot side method has to use @RequestParam for every field and
build object.

~~~
@RequestParam(name = "file", required = false) MultipartFile file, 
@RequestParam("title") String title,
@RequestParam("author") String author, 
@RequestParam("pages") int pages,
@RequestParam("publishDate") String pubDate
~~~

