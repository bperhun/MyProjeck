package ua.project.service.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ua.project.domain.SimpleFilter;
import ua.project.domain.mail.Mail;
import ua.project.entity.Category;
import ua.project.entity.UserDetails;
import ua.project.entity.UserEntity;
import ua.project.repository.UserRepository;
import ua.project.service.EmailService;
import ua.project.service.UserService;
import ua.project.service.utils.CustomFileUtils;
import ua.project.service.utils.TokenGenerator;



@Service
public class UserServiceImpl implements UserService {

	@Autowired private UserRepository userRepository;

	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private EmailService emailService;
	
	@Override
	@Transactional
	public void saveUser(UserEntity entity) {
		String password = entity.getPassword();
		
		entity.setPassword(passwordEncoder.encode(password));
		
		String token = TokenGenerator.generate(100);
		entity.setToken(token);
		entity.setActivated(false);
		entity.setUserDetails(new UserDetails());
		entity.setCategory(new Category());
		
		
		
		userRepository.save(entity);
		
		 CustomFileUtils.createFolder("user_"+entity.getId());
		 //
		 sendEmail(token, entity);
	}
	 
	private void sendEmail(String token,UserEntity entity) {
		String domain = "https://quiet-forest-38798.herokuapp.com";
		Mail mail = new Mail();
		mail.setTo(entity.getEmail());
		mail.setSubject("You are successfully registered");
		mail.setContent("Please virify your email by this link "
		+domain
		+"/verify?token="
		+token+"&userid="
		+entity.getId());
		emailService.sendMessage(mail);
	}

	@Override
	public UserEntity findUserById(int id) {
		return userRepository.findOne(id);
	}

	@Override
	public UserEntity findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}
	
	
	

	@Override
	public UserEntity findAllInf(String email) {
		
		return userRepository.findUserByEmail(email);
	}

	@Override
	public void updateUser(UserEntity entity) {
		userRepository.save(entity);
		
	}
	





//	@Override
//	public UserEntity deleteUser(String email) {
//		
//		return userRepository.delUser(email);
//	}

	@Override
	public void deleteUser(UserEntity entity) {
		userRepository.delete(entity);
		
	}

	@Override
	public Page<UserEntity> getPagebleUsers(int pageNumber, int pageSize, String sort, String sortByField) {
		PageRequest request =
				new PageRequest(
						pageNumber - 1,
						pageSize,
						Sort.Direction.ASC,
						"id");
		return userRepository.findAll(request);
	}

	@Override
	public List<UserEntity> findAllUsers() {
		
		return userRepository.findAll();
	}

	

}