# sb-mybook-angular V2

**The Angular client app for this app is branch MYBOOK-ANGULAR-CLIENT-V2**

This application uses file upload with formdata and multipart/form-data request with
Content-Type: multipart/form-data.

To parse formdata fields on Spring Boot side method has to use @RequestParam for every field and
build object.

This version is going to change file upload way. Use file upload separately from object Book's data fields sending.

## The way of image file uploading

In this version (V2) file the image uploading is done in to steps :

- send book json object with post/put request to create/update book object
- send multipart/form-data post request with image file data to update book for required id with the image. 

~~~
@RequestParam(name = "file", required = false) MultipartFile file, 
@RequestParam("title") String title,
@RequestParam("author") String author, 
@RequestParam("pages") int pages,
@RequestParam("publishDate") String pubDate
~~~

