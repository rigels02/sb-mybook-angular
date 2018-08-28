# sb-mybook-angular V2

**The Angular client app for this app is branch MYBOOK-ANGULAR-CLIENT-V2**

This application uses file upload with formdata and multipart/form-data request with
Content-Type: multipart/form-data.

To parse formdata fields on Spring Boot side method has to use @RequestParam for every field and
build object.

This version is going to change file upload way. Use file upload separately from object Book's data fields sending.

## File upload way
TODO...

~~~
@RequestParam(name = "file", required = false) MultipartFile file, 
@RequestParam("title") String title,
@RequestParam("author") String author, 
@RequestParam("pages") int pages,
@RequestParam("publishDate") String pubDate
~~~

