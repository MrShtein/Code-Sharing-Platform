package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SpringBootApplication
@RestController
public class CodeSharingPlatform {

    Storage storage;
    Configuration cfg;

    final CodeRepository codeRepository;

    @Autowired
    public CodeSharingPlatform(Storage storage, CodeRepository codeRepository) {
        this.storage = storage;
        cfgInit();
        this.codeRepository = codeRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(CodeSharingPlatform.class, args);
    }

    @GetMapping("/api/code/latest")
    public ResponseEntity<ArrayList<CodeResponse>> getLatestApi() {
        ArrayList<CodeResponse> responseList = getLatestResponseList();

        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "application/json");
        return new ResponseEntity<>(
                responseList, header, HttpStatus.OK
        );

    }

    private ArrayList<CodeResponse> getLatestResponseList() {
        ArrayList<Code> elements = performGetAllCodeSnippets();
        long index = elements.size() - 1;
        int count = 0;
        ArrayList<CodeResponse> list = new ArrayList<>();

        if (index >= 10) {
            for (long i = index; count != 10 && i >= 0; i--) {
                Code tempCode = elements.get((int) i);
                if (tempCode.isViewsRestricted() || tempCode.isTimeRestricted()) {
                    continue;
                } else {
                    CodeResponse response = new CodeResponse(
                            tempCode.getCode(), tempCode.getLoadDate(), tempCode.getTime(), tempCode.getViews());
                    list.add(response);
                    count++;
                }
            }
        } else {
            for (long i = index; i >= 0; i--) {
                Code tempCode = elements.get((int) i);
                if (tempCode.isViewsRestricted() || tempCode.isTimeRestricted()) {
                    continue;
                } else {
                    CodeResponse response = new CodeResponse(
                            tempCode.getCode(), tempCode.getLoadDate(), tempCode.getTime(), tempCode.getViews());
                    list.add(response);
                }
            }
        }
        return list;
    }

    private ArrayList<Code> performGetAllCodeSnippets() {
        ArrayList<Code> list = new ArrayList<>();
        codeRepository.findAll().forEach(list::add);
        return list;
    }

    @GetMapping("/code/latest")
    public ResponseEntity<String> getLatest() throws TemplateException, IOException {
        String html = htmlForLatestResponse();
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "text/html");
        return new ResponseEntity<>(
                html, header, HttpStatus.OK
        );
    }

    @PostMapping("/api/code/new")
    public ResponseEntity<ResponseTransfer> setCodeToStorage(@RequestBody Code newValue) {
        HttpHeaders header = new HttpHeaders();
        if ("Snippet #18".equals(newValue.getCode())) {
            System.out.println(newValue);
        }
        header.set("Content-Type", "application/json");
        newValue.setTimeRestricted(isTimeRestricted(newValue));
        newValue.setViewsRestricted(isViewsRestricted(newValue));
        String currentIndex = performCreateOperation(newValue);
        ResponseTransfer rt = new ResponseTransfer();
        rt.setId(currentIndex);
        return ResponseEntity
                .ok()
                .headers(header)
                .body(rt);
    }

    private String performCreateOperation(Code code) {
        String date = getDateAndTime();
        code.setLoadDate(date);
        code.setId(getNewUUID());
        code.setRestrictedTime(code.getTime());
        return codeRepository.save(code).getId();
    }

    @GetMapping("/code/{id}")
    public ResponseEntity<String> getHtml(@PathVariable String id) throws TemplateException, IOException {
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "text/html");
        String htmlForResponse = htmlForResponse(id);
        if ("".equals(htmlForResponse)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity
                .ok()
                .headers(header)
                .body(htmlForResponse);
    }

    private String htmlForResponse(String id) throws IOException, TemplateException {
        Optional<Code> code = performGetByIdOperation(id);
        if (code.isPresent() && checkTimeAndViewsInData(code.get())) {
            Code tempCode = code.get();
            CodeResponseForHtml codeResponse = new CodeResponseForHtml(
                    tempCode.getCode(), tempCode.getLoadDate(), tempCode.getTime(), tempCode.getViews(),
                    tempCode.isTimeRestricted() ? 1 : 0, tempCode.isViewsRestricted() ? 1 : 0
            );
            HashMap<String, CodeResponseForHtml> root = new HashMap<>();
            root.put("root", codeResponse);
            Template template = cfg.getTemplate("template.ftlh");
            StringWriter sw = new StringWriter();
            template.process(root, sw);
            return sw.toString();
        }
        return "";
    }

    @GetMapping("/api/code/{id}")
    public ResponseEntity<CodeResponse> getJson(@PathVariable String id) {
        Optional<Code> code = performGetByIdOperation(id);
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "application/json");
        if (code.isPresent() && checkTimeAndViewsInData(code.get())) {
            Code codeItem = code.get();
            CodeResponse codeResponse = new CodeResponse(
                    codeItem.getCode(), codeItem.getLoadDate(), codeItem.getTime(), codeItem.getViews()
            );
            return ResponseEntity
                    .ok()
                    .headers(header)
                    .body(codeResponse);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    private Optional<Code> performGetByIdOperation(String id) {
        return codeRepository.findById(id);
    }

    boolean checkTimeAndViewsInData(Code code) {
        if (!code.isTimeRestricted() && !code.isViewsRestricted()) {
            return true;
        }
        return checkTimeAndUpdateOrDelete(code) && checkViewAndUpdateOrDelete(code);
    }

    boolean checkTimeAndUpdateOrDelete(Code code) {
        if (!code.isTimeRestricted()) {
            return true;
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        LocalDateTime dtFromCode = LocalDateTime.parse(code.getLoadDate(), formatter);
        Instant nowInstant = now.toInstant(ZoneOffset.UTC);
        Instant dtFromCodeInstant = dtFromCode.toInstant(ZoneOffset.UTC);
        Duration duration = Duration.between(dtFromCodeInstant, nowInstant);
        long difference = duration.getSeconds();
        if (difference >= code.getRestrictedTime()) {
            codeRepository.delete(code);
            return false;
        }
        long timeFromCode = code.getRestrictedTime();
        code.setTime(timeFromCode - difference);
        return true;
    }

    boolean checkViewAndUpdateOrDelete(Code code) {
        if (!code.isViewsRestricted()) {
            return true;
        }
        long viewCount = code.getViews();
        if (viewCount <= 0) {
            codeRepository.delete(code);
            return false;
        } else {
            code.setViews(viewCount - 1);
            codeRepository.save(code);
            return true;
        }
    }

    @GetMapping("/code/new")
    public ResponseEntity<String> getFormForNewCode() throws IOException {
        String temp = cfg
                .getTemplate("form.ftlh")
                .toString();
        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", "text/html");
        return ResponseEntity
                .ok()
                .headers(header)
                .body(temp);
    }

    private String getDateAndTime() {
        String dateFormatter = "yyyy/MM/dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatter);
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }

    private String htmlForLatestResponse() throws IOException, TemplateException {
        ArrayList<CodeResponse> html = getLatestResponseList();
        HashMap<String, ArrayList<CodeResponse>> mapList = new HashMap<>();
        mapList.put("responses", html);
        Template template = cfg.getTemplate("latest.ftlh");
        StringWriter sw = new StringWriter();
        template.process(mapList, sw);
        return sw.toString();
    }


    private void cfgInit() {
        cfg = new Configuration(Configuration.VERSION_2_3_29);
        try {
            cfg.setDirectoryForTemplateLoading(new File("/home/mrshtein/IdeaProjects/Code Sharing Platform/Code Sharing Platform/task/src/platform/templates/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNewUUID() {
        return UUID.randomUUID().toString();
    }

    private boolean isTimeRestricted(Code code) {
        return code.getTime() > 0;
    }

    private boolean isViewsRestricted(Code code) {
        return code.getViews() > 0;
    }


}
