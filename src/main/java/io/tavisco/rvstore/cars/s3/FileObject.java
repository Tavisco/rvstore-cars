package io.tavisco.rvstore.cars.s3;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.model.S3Object;

@Data
@NoArgsConstructor
public class FileObject {
    private String objectKey;
    private Long size;

    public static FileObject from(S3Object s3Object) {
        FileObject file = new FileObject();
        if (s3Object != null) {
            file.setObjectKey(s3Object.key());
            file.setSize(s3Object.size());
        }
        return file;
    }

}
