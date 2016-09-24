package com.github.prasant.in;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SvgToPngController {

	@ResponseBody
	@RequestMapping(value = "/svgtopng", produces = MediaType.IMAGE_PNG_VALUE, method = RequestMethod.GET)
	public byte[] convertSvgToPng(@RequestParam("img") String img) throws IOException {
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(img, String.class);
		PNGTranscoder coder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(result.getBytes()));
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(ostream);
        try {
        	coder.transcode(input, output);
		} catch (TranscoderException e) {
			e.printStackTrace();
		}
        ostream.flush();
		return ostream.toByteArray();
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/svgtopngzip", produces="application/zip", method = RequestMethod.GET)
	public byte[] convertSvgToPngAsZip(@RequestParam("img") String img, HttpServletResponse response) throws IOException {
		
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(img, String.class);
		PNGTranscoder coder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(result.getBytes()));
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(ostream);
        try {
        	coder.transcode(input, output);
		} catch (TranscoderException e) {
			e.printStackTrace();
		}
        ostream.flush();
		
        response.setContentType("application/zip");
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"img.zip\"");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
        ArrayList<File> files = new ArrayList<>(2);
        
        File outputFile = new File("india.png");
        try ( FileOutputStream outputStream = new FileOutputStream(outputFile); ) {
            outputStream.write(ostream.toByteArray());  //write the bytes and your done. 
        } catch (Exception e) {
            e.printStackTrace();
        }
        files.add(outputFile);

        for (File file : files) {
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
	}
	
	
	
	
	
	

}
