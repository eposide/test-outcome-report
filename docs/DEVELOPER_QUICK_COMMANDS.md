# Developer Quick Commands

## Testing the Theme

### Start the Application
```bash
mvn clean spring-boot:run
```

### View Dashboard
```
http://localhost:8080/api/
```

### View Component Showcase
```
http://localhost:8080/api/showcase
```

### Rebuild Project
```bash
mvn clean package
```

---

## File Editing

### Edit Theme Colors
```
File: src/main/resources/static/css/theme.css
Lines: 2-18 (CSS variables in :root)
```

### Edit Dashboard Template
```
File: src/main/resources/templates/dashboard.html
Lines: All
```

### Edit Upload Form
```
File: src/main/resources/templates/upload.html
Lines: All
```

### Edit Header Component
```
File: src/main/resources/templates/fragments/header.html
Lines: All
```

### Edit Footer Component
```
File: src/main/resources/templates/fragments/footer.html
Lines: All
```

---

## Browser DevTools

### Inspect Element
```
F12                         Open DevTools
Ctrl+Shift+C                Open element inspector
```

### Check CSS Loading
```
1. Open DevTools (F12)
2. Go to Network tab
3. Look for theme.css
4. Verify 200 status (not 404)
```

### Test Mobile Responsive
```
F12                         Open DevTools
Ctrl+Shift+M                Toggle device emulation
320px, 768px, 1024px        Common breakpoints
```

### Debug Thymeleaf
```
1. Check browser source
2. Look for th: attributes
3. Verify links render correctly
```

---

## Common Issues & Fixes

### CSS Not Loading
```
Problem: theme.css returns 404
Solution 1: Check file exists at src/main/resources/static/css/theme.css
Solution 2: Restart Spring Boot after creating file
Solution 3: Verify path in template: th:href="@{/css/theme.css}"
```

### Templates Not Found
```
Problem: 404 when accessing template
Solution 1: Check @GetMapping routes in controller
Solution 2: Verify template exists in src/main/resources/templates/
Solution 3: Return statement matches filename without .html
```

### Styles Not Applied
```
Problem: Classes exist but not styled
Solution 1: Check CSS loads (DevTools → Network)
Solution 2: Verify class names match exactly
Solution 3: Check browser console for CSS errors
```

### Fragment Not Showing
```
Problem: Header/footer fragments not rendering
Solution 1: Check fragment location: src/main/resources/templates/fragments/
Solution 2: Verify Thymeleaf namespace: xmlns:th="..."
Solution 3: Check fragment include syntax: th:replace="fragments/header :: header"
```

---

## Database Commands

### Connect to Database
```bash
# If using H2 (in-memory)
# Visit: http://localhost:8080/h2-console

# Connection URL: jdbc:h2:mem:testdb
# User: sa
# Password: (leave blank)
```

### View Test Runs
```sql
SELECT * FROM TEST_RUN;
```

### View Test Cases
```sql
SELECT * FROM TEST_CASE;
```

### Clear Data
```sql
DELETE FROM TEST_CASE;
DELETE FROM TEST_RUN;
```

---

## Git Commands

### Add Theme Files
```bash
git add src/main/resources/static/css/theme.css
git add src/main/resources/templates/
git add INDEX.md THEME*.md
git commit -m "Add complete theme with documentation"
git push
```

### View Changes
```bash
git status
git diff src/main/resources/static/css/theme.css
```

---

## Maven Commands

### Build Project
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

### Skip Tests
```bash
mvn clean package -DskipTests
```

### Run Specific Test
```bash
mvn test -Dtest=ParserRegistryTest
```

---

## Useful VS Code Shortcuts

### Format HTML/CSS
```
Shift+Alt+F        Auto-format current file
```

### Search Files
```
Ctrl+P              Quick file open
Ctrl+Shift+F        Search entire workspace
```

### Go to Definition
```
Ctrl+Click          Jump to class/method definition
```

### Rename Symbol
```
F2                  Rename all occurrences
```

---

## Thymeleaf Debugging

### View Rendered HTML
```
F12                 Open DevTools
Elements tab        Inspect actual HTML
Look for th: attrs  Check Thymeleaf processing
```

### Check Model Data
```html
<!-- Add to template to debug model -->
<div th:text="${#collections.size(recentRuns)}"></div>
```

### Verify Fragment Links
```html
<!-- Check link generation -->
<a th:href="@{/api/}">Link</a>
```

---

## Performance Testing

### Check CSS Size
```bash
# View file size
ls -lh src/main/resources/static/css/theme.css

# Check gzipped size (if using compression)
gzip -c src/main/resources/static/css/theme.css | wc -c
```

### Page Load Time
```
F12 → Performance tab → Record load
Look for:
- CSS parse time
- Render time
- Layout time
```

### Mobile Performance
```
F12 → Performance tab
Throttle to "Slow 3G"
Test page load time
```

---

## Common Spring Boot Properties

### Edit Application Configuration
```
File: src/main/resources/application.yml
```

### Context Path (if needed)
```yaml
server:
  servlet:
    context-path: /api
```

### Port Configuration
```yaml
server:
  port: 8080
```

### Template Location
```yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
```

---

## Documentation Quick Links

### Quick Lookup
```
THEME_QUICK_REFERENCE.md
- Component classes
- Color variables
- Utility classes
```

### Full Reference
```
THEME.md
- All components explained
- Customization guide
- Accessibility info
```

### Implementation
```
THEME_IMPLEMENTATION_GUIDE.md
- Step-by-step setup
- Controller examples
- Troubleshooting
```

### Visual Guide
```
THEME_VISUAL_GUIDE.md
- Component visuals
- Color palette
- Layout diagrams
```

---

## Quick CSS Customizations

### Change Primary Color (Edit theme.css)
```css
:root {
    --primary-color: #2b6cb0;  ← Change this
    --primary-light: #3d7ec8;  ← And this
    --primary-dark: #1e4d7b;   ← And this
}
```

### Add Custom Class
```css
.my-custom-class {
    background-color: var(--primary-color);
    padding: 1rem;
    border-radius: var(--border-radius);
}
```

### Override Button Styles
```css
.btn-primary:hover {
    background-color: var(--primary-light);
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
}
```

---

## Testing Checklist

### Functionality
- [ ] Dashboard loads without errors
- [ ] Upload form appears
- [ ] Navigation links work
- [ ] Footer displays
- [ ] All pages load

### Styling
- [ ] Colors display correctly
- [ ] Fonts render properly
- [ ] Spacing looks right
- [ ] Components align well
- [ ] Shadows/borders visible

### Responsive
- [ ] Works at 320px (mobile)
- [ ] Works at 768px (tablet)
- [ ] Works at 1024px (desktop)
- [ ] Touch targets are large enough
- [ ] Text is readable

### Browser
- [ ] Chrome ✓
- [ ] Firefox ✓
- [ ] Safari ✓
- [ ] Edge ✓

---

## Useful Resources

### Documentation
- INDEX.md - Navigation guide
- THEME.md - Complete reference
- THEME_QUICK_REFERENCE.md - Quick lookup
- THEME_IMPLEMENTATION_GUIDE.md - How-to guide

### Pages
- showcase.html - Component showcase
- dashboard.html - Dashboard example
- upload.html - Form example
- test-details.html - Details page example

### Tools
- Spring Boot DevTools - Hot reload
- Browser DevTools - CSS/HTML inspection
- Thymeleaf Inspector - Template debugging

---

## Command Line Tips

### Check File Exists
```bash
test -f src/main/resources/static/css/theme.css && echo "File exists" || echo "File missing"
```

### View File Line Count
```bash
wc -l src/main/resources/static/css/theme.css
```

### Search for Text in Files
```bash
grep -r "theme.css" src/main/resources/templates/
```

### Find All HTML Files
```bash
find src/main/resources/templates -name "*.html"
```

---

## Troubleshooting Workflow

### Issue: Styles Not Applying

1. **Check CSS loads**
   ```
   F12 → Network → Search "theme.css" → Check status 200
   ```

2. **Verify path**
   ```
   HTML: th:href="@{/css/theme.css}"
   File: src/main/resources/static/css/theme.css
   ```

3. **Restart application**
   ```bash
   # Stop Spring Boot (Ctrl+C)
   mvn clean spring-boot:run
   ```

4. **Clear browser cache**
   ```
   F12 → Settings → Check "Disable cache"
   ```

### Issue: Template Not Found

1. **Check controller route**
   ```java
   @GetMapping("/")  // ← Verify route
   public String dashboard() {
       return "dashboard";  // ← Template name
   }
   ```

2. **Verify file exists**
   ```
   src/main/resources/templates/dashboard.html
   ```

3. **Check view resolver**
   ```
   File: src/main/resources/application.yml
   Ensure Thymeleaf is configured
   ```

4. **Restart application**
   ```bash
   mvn clean spring-boot:run
   ```

---

## Next Developer

If taking over this project:

1. Read: **INDEX.md**
2. Read: **THEME_IMPLEMENTATION_GUIDE.md**
3. Review: **THEME.md**
4. Check: `src/main/resources/static/css/theme.css`
5. Test: `http://localhost:8080/api/showcase`
6. Start modifying!

---

**Theme Version:** 1.0.0  
**Last Updated:** 2026-05-21

