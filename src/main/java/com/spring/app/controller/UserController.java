package com.spring.app.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.app.entity.User;
import com.spring.app.service.UserService;

@Controller("/")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;
	private String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

	@GetMapping("/")
	public String getUsersList(@RequestParam(defaultValue = "1") Integer pageNo,
			@RequestParam(defaultValue = "5") Integer pageSize, Model model) {

		log.info("*************** Calling `@GetMapping(\"/\")` **************** ");

		model.addAttribute("users", userService.getUsersByPagination(pageNo, pageSize));
		model.addAttribute("totalPages", ((int) (userService.getUsersCount() / pageSize)) + 1);
		model.addAttribute("currentPage", pageNo);

		log.info("*************** Exit `@GetMapping(\"/\")` ***************** ");

		return "users";
	}

	@GetMapping("/new")
	public String createNewUser(Model model) {

		log.info("*************** Calling `@GetMapping(\"/new\")` **************** ");

		User user = new User();
		model.addAttribute("user", user);

		log.info("*************** Exit `@GetMapping(\"/new\")` **************** ");

		return "create-new-user";
	}

	@PostMapping("/users")
	public String saveUser(@RequestParam("name") String name, @RequestParam("dob") Date dob,
			@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam(value = "image", required = false) MultipartFile image, RedirectAttributes redirectAttributes,
			Model model) throws IOException {

		log.info("*************** Calling `@PostMapping(\"/users\")` **************** ");

		String encryptedPassword = userService.encryptPassword(password);
		String uploadedImage = userService.uploadImage(image);

		User user = new User(name, dob, email, encryptedPassword, uploadedImage);
		userService.saveUser(user);

		redirectAttributes.addFlashAttribute("flashMessage", "User created successfully!");

		log.info("*************** Exit `@PostMapping(\"/users\")` **************** ");

		return "redirect:/";
	}

	@GetMapping("/update/{id}")
	public String updateUser(@PathVariable("id") Long id, Model model) {

		log.info("************** Calling `@GetMapping(\"/update/{id}\")` ***************** ");

		model.addAttribute("user", userService.getUserById(id));

		log.info("***************** Exit `@GetMapping(\"/update/{id}\")` ***************** ");

		return "update-user";
	}

	@PostMapping("/users/{id}")
	public String saveUpdatedUser(@PathVariable("id") Long id, @RequestParam("name") String name,
			@RequestParam("dob") Date dob, @RequestParam("email") String email,
			@RequestParam("password") String password,
			@RequestParam(value = "image", required = false) MultipartFile image,
			@RequestParam(value = "oldImage", required = false) String oldImageName, Model model,
			RedirectAttributes redirectAttributes) throws IOException {

		log.info("*********************** Calling `@PostMapping(\"/users/{id}\")` ***************** ");

		User user = userService.getUserById(id);
		String encryptedPassword = "";
		if (password.equals(user.getPassword())) {
			encryptedPassword = password;
		} else {
			encryptedPassword = userService.encryptPassword(password);
		}

		String uploadedImage = userService.updateImage(oldImageName, image);

		user.setName(name);
		user.setEmail(email);
		user.setPassword(encryptedPassword);
		user.setDob(dob);
		if (uploadedImage != null) {
			user.setImage(uploadedImage);
		}

		userService.updateUser(user);
		redirectAttributes.addFlashAttribute("flashMessage", "User updated successfully!");

		log.info("********************* Exit `@PostMapping(\"/users/{id}\")` ***************** ");

		return "redirect:/";
	}

	@GetMapping("users/{id}")
	public String deleteUser(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {

		log.info("******************  Calling `@GetMapping(\"/users/{id}\")` ***************** ");

		userService.deleteUser(id);
		redirectAttributes.addFlashAttribute("flashMessage", "User deleted successfully!");

		log.info("***************** Exit `@GetMapping(\"/users/{id}\")` ********************* ");

		return "redirect:/";
	}

	@GetMapping("/uploads/{imageName}")
	public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
		try {

			Path imagePath = Paths.get(UPLOAD_DIR).resolve(imageName);
			Resource resource = new UrlResource(imagePath.toUri());

			if (resource.exists() && resource.isReadable()) {
				MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.IMAGE_JPEG);
				return ResponseEntity.ok().contentType(mediaType).body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
