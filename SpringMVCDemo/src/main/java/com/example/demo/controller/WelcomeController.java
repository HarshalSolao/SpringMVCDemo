package com.example.demo.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.errorhandler.StorageException;
import com.example.demo.model.User;

@Controller
@RequestMapping("/welcome")
public class WelcomeController {

	@Value("${storage.location}")
	private String location;

	@GetMapping({ "/", "/index", "/index.html" })
	public String getWelcomePage(Model model) {
		List<Path> paths = loadAll().collect(Collectors.toList());
		model.addAttribute("serverTime", new Date());
		List<String> pathList = new ArrayList<>();
		//
		for (Path path : paths) {
			pathList.add(ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/welcome/download/").path(path.getFileName().toString()).toUriString());
		}

		model.addAttribute("files", pathList);
		/*model.addAttribute("files", loadAll().map(path -> ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/welcome/download/").path(path.getFileName().toString()).toUriString()).collect(Collectors.toList()));
		*/
		return "index";
	}

	@GetMapping({ "/signin" })
	public String getSignInPage(Model model) {
		model.addAttribute("serverTime", new Date());
		List<User> list = new ArrayList<>();
		list.add(new User("Java", "Spring"));
		list.add(new User("Pythom", "M"));
		list.add(new User("Ruby", "M"));
		list.add(new User("CPP", "F"));

		model.addAttribute("userList", list);

		return "signin";
	}

	@PostMapping({ "/save" })
	public String getSignInPageSaveData(@ModelAttribute("user") User user) {
		System.out.println(user);
		return "success";
	}

	@PostMapping("/upload-file")
	public String uploadFile(@RequestParam("file") MultipartFile file) {
		String name = store(file);

		String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/welcome/download/").path(name).toUriString();
		System.out.println("URL : " + uri);
		return "index";
	}
	
	@GetMapping("/download/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

		Resource resource = loadAsResource(filename);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	public String store(MultipartFile file) {
		Path path = Paths.get(location);
		//qwertyuiop[
		System.out.println(location + " -- " + file.getOriginalFilename());
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		System.out.println(filename);
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, path.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}

		return filename;

	}

	public Stream<Path> loadAll() {
		Path path = Paths.get(location);
		try {
			return Files.walk(path, 1).filter(loc -> !loc.equals(path)).map(path::relativize);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new FileNotFoundException("Could not read file: " + filename);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public Path load(String filename) {
		Path path = Paths.get(location);
		 return path.resolve(filename);
	}


}
