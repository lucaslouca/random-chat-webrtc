package com.fizzbuzzer.webrtc.service.impl;

import com.fizzbuzzer.webrtc.dao.request.GenericResponseDTO;
import com.fizzbuzzer.webrtc.entities.User;
import com.fizzbuzzer.webrtc.repository.RoleRepository;
import com.fizzbuzzer.webrtc.repository.UserRepository;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Value("${upload.path}")
    private String path;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user == null) {
            throw new UsernameNotFoundException(String.format("The username %s doesn't exist", username));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        });


        UserDetails userDetails = new org.springframework.security.core.userdetails.
                User(user.getUsername(), user.getPassword(), authorities);


        return userDetails;
    }


    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    public Long getProcessingUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user != null) {
            return user.getId();
        } else {
            return null;
        }
    }


    private boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public GenericResponseDTO changeEmail(String email, String tokenId) {
        if (isValidEmailAddress(email)) {
            Long userId = getProcessingUserId();
            User userInDb = userRepository.findById(userId).get();
            userInDb.setEmail(email);
            userRepository.save(userInDb);
            return new GenericResponseDTO("Email change was successful.", true);
        } else {
            return new GenericResponseDTO("Email is invalid!", false);
        }
    }

    public GenericResponseDTO changePicture(MultipartFile file, String tokenId) {
        if (!file.isEmpty()) {
            try {
                Long userId = getProcessingUserId();
                User userInDb = userRepository.findById(userId).get();

                String originalFileName = file.getOriginalFilename();
                String fileName = userId + originalFileName.substring(originalFileName.lastIndexOf("."));
                InputStream is = file.getInputStream();

                Files.copy(is, Paths.get(path + fileName), StandardCopyOption.REPLACE_EXISTING);

                userInDb.setPicture(fileName);
                userRepository.save(userInDb);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new GenericResponseDTO("Picture change was successful.", true);
        } else {
            return new GenericResponseDTO("Picture change failed.", false);
        }
    }


    public String getProccesingUserPictureURL() {
        Long userId = getProcessingUserId();
        User userInDb = userRepository.findById(userId).get();
        String fileName = userInDb.getPicture();
        return fileName;
    }

    public String getPictureForProccesingUserPicture(String fileName) {
        String base64Image = "";
        Long userId = getProcessingUserId();
        User userInDb = userRepository.findById(userId).get();
        String dbFileName = userInDb.getPicture();

        if (dbFileName.equals(fileName)) {
            File file = new File(path + dbFileName);
            try (FileInputStream imageInFile = new FileInputStream(file)) {
                // Reading a Image file from file system
                byte imageData[] = new byte[(int) file.length()];
                imageInFile.read(imageData);
                base64Image = Base64.getEncoder().encodeToString(imageData);
            } catch (FileNotFoundException e) {
                System.out.println("Image not found" + e);
            } catch (IOException ioe) {
                System.out.println("Exception while reading the Image " + ioe);
            }

        } else {
            throw new RuntimeException("Requested filename does not match stored filename.");
        }

        return base64Image;
    }


    public String getProccesingUserUsername() {
        Long userId = getProcessingUserId();
        User userInDb = userRepository.findById(userId).get();
        return userInDb.getUsername();
    }

    public String getProccesingUserEmail() {
        Long userId = getProcessingUserId();
        User userInDb = userRepository.findById(userId).get();
        return userInDb.getEmail();
    }


}
