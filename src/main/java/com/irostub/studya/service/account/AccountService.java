package com.irostub.studya.service.account;

import com.irostub.studya.controller.adapter.UserAccount;
import com.irostub.studya.controller.form.NotificationForm;
import com.irostub.studya.controller.form.ProfileForm;
import com.irostub.studya.controller.form.SignupForm;
import com.irostub.studya.domain.Account;
import com.irostub.studya.domain.Tag;
import com.irostub.studya.mapper.AccountMapper;
import com.irostub.studya.repository.AccountRepository;
import com.irostub.studya.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;

    @Transactional
    public Account processSaveNewAccount(SignupForm signupForm) {
        Account account = saveNewAccount(signupForm);
        sendVerifyEmail(account);
        return account;
    }

    public Account saveNewAccount(SignupForm signupForm) {
        Account account = accountMapper.signupFormToAccount(signupForm);
        account.setStudyCreatedByWeb(true);
        account.setStudyEnrollmentResultByWeb(true);
        account.setStudyUpdatedByWeb(true);
        return accountRepository.save(account);
    }

    public void sendVerifyEmail(Account saveAccount) {
        saveAccount.generateEmailCheckToken();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(saveAccount.getEmail());
        mail.setSubject("Studya 인증 요청 메일입니다.");
        mail.setText("/check-email-token?email=" + saveAccount.getEmail() + "&token=" + saveAccount.getEmailCheckToken());

        javaMailSender.send(mail);
    }

    @Transactional
    public Account verifyEmail(String email, String token, Model model){
        Account findAccount = accountRepository.findByEmail(email).orElse(null);
        if (findAccount == null) {
            model.addAttribute("error", "account not found");
            return null;
        }
        if(!findAccount.getEmailCheckToken().equals(token)){
            model.addAttribute("error", "token not found");
            return null;
        }
        findAccount.setEmailVerified(true);
        findAccount.setJoinedAt(LocalDateTime.now());
        model.addAttribute("count", accountRepository.count());
        model.addAttribute("name", findAccount.getNickname());
        return findAccount;
    }

    public void sendLoginMail(Account account) {
        account.generateEmailCheckToken();

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(account.getEmail());
        simpleMailMessage.setSubject("studya 이메일 로그인 메일입니다.");
        simpleMailMessage.setText("/login-by-email?email=" + account.getEmail() + "&token=" + account.getEmailCheckToken());
        javaMailSender.send(simpleMailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByNickname(username).orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        return new UserAccount(account);
    }

    public void updateProfile(Account account, ProfileForm profileForm) {
        accountMapper.updateFromProfileForm(profileForm, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String password) {
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
    }

    public void updateNotification(Account account, NotificationForm notificationForm) {
        accountMapper.updateFromNotificationForm(notificationForm, account);
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
    }

    public Optional<Account> loginByEmail(String email, String token) {
        return accountRepository.findByEmail(email).filter(account -> account.getEmailCheckToken().equals(token));
    }

    @Transactional
    public void addTag(Account account, String title) {
        Optional<Account> optionalAccount = accountRepository.findById(account.getId());
        Tag tag = tagRepository.findByTitle(title).orElseGet(
                ()-> tagRepository.save(Tag.builder().title(title).build()));
        optionalAccount.ifPresent(a->a.getTags().add(tag));
    }

    public Set<Tag> getTags(Long id) {
        return accountRepository.findById(id).orElseThrow().getTags();
    }

    @Transactional
    public ResponseEntity<Object> removeTag(Account account, String title) {
        Optional<Account> optionalAccount = accountRepository.findById(account.getId());
        Optional<Tag> byTitle = tagRepository.findByTitle(title);

        if(byTitle.isEmpty()) return ResponseEntity.badRequest().build();

        optionalAccount.ifPresent(a ->
            byTitle.ifPresent(t -> a.getTags().remove(t)));

        return ResponseEntity.ok().build();
    }

    public List<Tag> findTags(String input) {
        System.out.println("input = " + input);
        List<Tag> byTitleStartsWith = tagRepository.findByTitleStartsWith(input);
        if (byTitleStartsWith.isEmpty()) {
            System.out.println("whynull????");
        }
        for (Tag tag : byTitleStartsWith) {
            System.out.println(tag.getTitle());
        }
        return byTitleStartsWith;
    }
}
