package com.floss.odontologia.service.interfaces;

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
