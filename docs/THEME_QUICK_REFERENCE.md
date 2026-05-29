# Theme Quick Reference

## Files Created

```
src/main/resources/
├── static/
│   └── css/
│       └── theme.css          # Main theme stylesheet
└── templates/
    ├── layout.html             # Base layout template
    ├── dashboard.html          # Dashboard page
    ├── upload.html             # Upload form page
    ├── test-details.html       # Test details page
    └── fragments/
        ├── header.html         # Header component
        └── footer.html         # Footer component

THEME.md                         # Complete theme documentation
```

## Quick Start

### 1. Include Theme in Your Template

```html
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Your Page</title>
    <link rel="stylesheet" th:href="@{/css/theme.css}" />
</head>
```

### 2. Use Header and Footer

```html
<body>
    <header th:replace="fragments/header :: header"></header>
    
    <main>
        <div class="container">
            <!-- Your content -->
        </div>
    </main>
    
    <footer th:replace="fragments/footer :: footer"></footer>
</body>
```

### 3. Common Components

#### Card
```html
<div class="card">
    <div class="card-header">Title</div>
    <div class="card-body">Content</div>
</div>
```

#### Badges
```html
<span class="badge badge-success">PASSED</span>
<span class="badge badge-danger">FAILED</span>
<span class="badge badge-warning">SKIPPED</span>
```

#### Buttons
```html
<button class="btn btn-primary">Primary</button>
<button class="btn btn-secondary">Secondary</button>
<a href="#" class="btn btn-primary btn-small">Small</a>
```

#### Alerts
```html
<div class="alert alert-success"><strong>Success!</strong> Done.</div>
<div class="alert alert-danger"><strong>Error!</strong> Failed.</div>
<div class="alert alert-warning"><strong>Warning!</strong> Be careful.</div>
<div class="alert alert-info"><strong>Info:</strong> Something to know.</div>
```

#### Forms
```html
<div class="form-group">
    <label for="email">Email</label>
    <input type="email" id="email" name="email" />
</div>
```

#### Stats Grid
```html
<div class="stats-grid">
    <div class="stat-box">
        <div class="stat-label">Total Tests</div>
        <div class="stat-value">150</div>
    </div>
    <div class="stat-box">
        <div class="stat-label">Pass Rate</div>
        <div class="stat-value">95%</div>
    </div>
</div>
```

#### Tables
```html
<table>
    <thead>
        <tr>
            <th>Name</th>
            <th>Status</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Test 1</td>
            <td><span class="badge badge-success">PASSED</span></td>
        </tr>
    </tbody>
</table>
```

## Color Variables

```css
--primary-color: #2b6cb0
--success-color: #10b981
--danger-color: #ef4444
--warning-color: #f59e0b
--info-color: #06b6d4
```

## Utility Classes

| Class | Purpose |
|-------|---------|
| `.container` | Max-width wrapper (1200px) |
| `.text-center` | Center text |
| `.text-right` | Right-align text |
| `.text-muted` | Gray text |
| `.mt-1, .mt-2, .mt-3, .mt-4` | Margin top |
| `.mb-1, .mb-2, .mb-3, .mb-4` | Margin bottom |
| `.p-2, .p-3, .p-4` | Padding |
| `.flex` | Flex display |
| `.flex-between` | Flex with space-between |
| `.flex-center` | Flex with centered items |
| `.gap-1, .gap-2, .gap-3` | Gap between items |

## Responsive Breakpoint

Mobile adjustments at `max-width: 768px`:
- Navigation wraps
- Table padding reduces
- Stats grid becomes single column

## Navigation Links (Update Your Controllers)

The header includes navigation links that should map to your controllers:

```html
<a th:href="@{/api/}" title="Dashboard">Dashboard</a>
<a th:href="@{/api/upload}" title="Upload">Upload</a>
<a th:href="@{/api/tests}" title="Tests">Tests</a>
```

Ensure your Spring Boot controllers handle these routes.

## Example: Complete Page

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>My Page</title>
    <link rel="stylesheet" th:href="@{/css/theme.css}" />
</head>
<body>
    <header th:replace="fragments/header :: header"></header>
    
    <main>
        <div class="container">
            <div class="card">
                <div class="card-header">My Content</div>
                <div class="card-body">
                    <p>Welcome to the Test Outcome Report!</p>
                    
                    <div class="stats-grid mt-3">
                        <div class="stat-box">
                            <div class="stat-label">Tests Run</div>
                            <div class="stat-value">1,234</div>
                        </div>
                        <div class="stat-box">
                            <div class="stat-label">Pass Rate</div>
                            <div class="stat-value">98%</div>
                        </div>
                    </div>
                    
                    <button class="btn btn-primary mt-3">Get Started</button>
                </div>
            </div>
        </div>
    </main>
    
    <footer th:replace="fragments/footer :: footer"></footer>
</body>
</html>
```

## Tips

1. **Always include viewport meta tag** for responsive design
2. **Use `.container`** to wrap main content for consistent max-width
3. **Cards are flexible** - nest them for complex layouts
4. **Badges work best with table cells** for status indicators
5. **Stats grid adapts** to screen size automatically
6. **Form groups ensure consistent spacing** between fields

## Dark Mode Support (Future)

Add this to theme.css for dark mode support:

```css
@media (prefers-color-scheme: dark) {
    :root {
        --gray-900: #ffffff;
        --gray-800: #f3f4f6;
        --gray-50: #111827;
        /* ... adjust other colors */
    }
}
```

## Further Customization

Edit `src/main/resources/static/css/theme.css` to:
- Change colors in `:root` section
- Adjust spacing and sizing
- Modify border radius
- Update shadows
- Add new components

For complete documentation, see `THEME.md`.

