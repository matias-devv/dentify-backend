package com.dentify.integration.cloudinary;

public interface ICloudinaryService {

    /**
     * Upload PDF to Cloudinary
     */
    String uploadPDF(String filename, byte[] fileBytes);

    /**
     * Delete file by filename
     */
    void delete(String filename);

}
