# Test Outcome Report Theme Documentation

## Overview

The Test Outcome Report theme is a modern, responsive, and accessible design system built with CSS custom properties (variables) for easy customization.

## Color Palette

### Primary Colors
- **Primary**: `#2b6cb0` - Main brand color for buttons, links, and accents
- **Primary Light**: `#3d7ec8` - Hover state for primary elements
- **Primary Dark**: `#1e4d7b` - Active/pressed state

### Status Colors
- **Success**: `#10b981` - Green for passed tests
- **Danger**: `#ef4444` - Red for failed tests
- **Warning**: `#f59e0b` - Amber for skipped tests
- **Info**: `#06b6d4` - Cyan for information

### Gray Scale
- **Gray 50-900**: Neutral colors from lightest to darkest
- Used for backgrounds, text, borders, and disabled states

## CSS Variables

All theme values are defined as CSS custom properties in `:root`:

```css
--primary-color: #2b6cb0;
--success-color: #10b981;
--danger-color: #ef4444;
--warning-color: #f59e0b;
--border-radius: 6px;
--shadow-sm: 0 1px 2px 0 rgba(0, 0, 0, 0.05);
```

### Customizing Colors

Edit `src/main/resources/static/css/theme.css` to change theme colors:

```css
:root {
    --primary-color: #your-color-here;
    --success-color: #your-color-here;
    /* ... etc */
}
```

## Component Classes

### Cards
- `.card` - Main card container with white background and shadow
- `.card-header` - Card title/header section
- `.card-body` - Card content area

```html
<div class="card">
    <div class="card-header">Card Title</div>
    <div class="card-body">
        <!-- Content here -->
    </div>
</div>
```

### Badges
- `.badge` - Base badge style
- `.badge-success` - Green badge for passed items
- `.badge-danger` - Red badge for failed items
- `.badge-warning` - Amber badge for warnings
- `.badge-info` - Cyan badge for information
- `.badge-neutral` - Gray badge for neutral states

```html
<span class="badge badge-success">PASSED</span>
<span class="badge badge-danger">FAILED</span>
<span class="badge badge-warning">SKIPPED</span>
```

### Buttons
- `.btn` - Base button style
- `.btn-primary` - Primary action button (blue)
- `.btn-secondary` - Secondary action button (gray)
- `.btn-success` - Success button (green)
- `.btn-danger` - Danger button (red)
- `.btn-small` - Smaller button variant

```html
<button class="btn btn-primary">Save</button>
<a href="#" class="btn btn-secondary btn-small">Cancel</a>
```

### Alerts
- `.alert` - Base alert container
- `.alert-info` - Information alert (blue)
- `.alert-success` - Success alert (green)
- `.alert-warning` - Warning alert (amber)
- `.alert-danger` - Danger alert (red)

```html
<div class="alert alert-success">
    <strong>Success!</strong> Your changes have been saved.
</div>
```

### Statistics Display
- `.stats-grid` - Responsive grid for stat boxes
- `.stat-box` - Individual stat box
- `.stat-label` - Label for the stat
- `.stat-value` - Large value display

```html
<div class="stats-grid">
    <div class="stat-box">
        <div class="stat-label">Total Tests</div>
        <div class="stat-value">150</div>
    </div>
</div>
```

### Forms
- Standard form elements (input, textarea, select) are styled globally
- `.form-group` - Wrapper for form field + label

```html
<div class="form-group">
    <label for="name">Name</label>
    <input type="text" id="name" name="name" />
</div>
```

### Tables
- `table` - Standard table styling with hover effects
- `thead` - Light gray background for header
- `tbody tr:hover` - Subtle highlight on row hover

### Utility Classes

**Text Alignment**
- `.text-center` - Center text
- `.text-right` - Right-align text

**Text Colors**
- `.text-muted` - Gray, subtle text

**Spacing (Margin)**
- `.mt-1`, `.mt-2`, `.mt-3`, `.mt-4` - Margin top
- `.mb-1`, `.mb-2`, `.mb-3`, `.mb-4` - Margin bottom

**Spacing (Padding)**
- `.p-2`, `.p-3`, `.p-4` - Padding

**Flexbox**
- `.flex` - Display flex
- `.flex-between` - Justify space-between with centered items
- `.flex-center` - Center flex items
- `.gap-1`, `.gap-2`, `.gap-3` - Gap between flex items

## Responsive Design

The theme is mobile-first and responsive:

- Breakpoint: `max-width: 768px` for mobile adjustments
- Navigation wraps on smaller screens
- Tables remain readable on mobile with adjusted padding
- Grid layouts adapt to screen size

## Layout Structure

### Recommended HTML Structure

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Page Title</title>
    <link rel="stylesheet" th:href="@{/css/theme.css}" />
</head>
<body>
    <header th:replace="fragments/header :: header"></header>
    
    <main>
        <div class="container">
            <div class="card">
                <div class="card-header">Page Title</div>
                <div class="card-body">
                    <!-- Your content here -->
                </div>
            </div>
        </div>
    </main>
    
    <footer th:replace="fragments/footer :: footer"></footer>
</body>
</html>
```

## Files Included

1. **theme.css** - Main stylesheet with all theme definitions
2. **layout.html** - Base layout template for Thymeleaf (requires layout dialect)
3. **dashboard.html** - Dashboard page with test results table
4. **upload.html** - Upload form for test results
5. **test-details.html** - Detailed view of a single test run
6. **fragments/header.html** - Reusable header component
7. **fragments/footer.html** - Reusable footer component

## Usage Examples

### Dashboard Table
The dashboard uses cards, tables, and badges to display test results:

```html
<div class="card">
    <div class="card-header">Test Results</div>
    <div class="card-body">
        <table>
            <thead>
                <tr>
                    <th>Project</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>My Tests</td>
                    <td><span class="badge badge-success">PASSED</span></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
```

### Form Page
```html
<div class="card">
    <div class="card-header">Upload Results</div>
    <div class="card-body">
        <form>
            <div class="form-group">
                <label>Project Name</label>
                <input type="text" />
            </div>
            <button class="btn btn-primary">Submit</button>
        </form>
    </div>
</div>
```

## Accessibility

The theme follows WCAG 2.1 guidelines:
- Sufficient color contrast ratios
- Semantic HTML structure
- Focus states for interactive elements
- Proper label associations with form inputs

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)
- Mobile browsers (iOS Safari, Chrome Mobile)

## Customization Tips

### Change Primary Color
Edit the `:root` section in `theme.css`:
```css
--primary-color: #your-brand-color;
--primary-light: #lighter-shade;
--primary-dark: #darker-shade;
```

### Adjust Border Radius
```css
--border-radius: 8px; /* More rounded */
/* or */
--border-radius: 2px; /* More sharp */
```

### Dark Mode (Future Enhancement)
Consider adding a media query:
```css
@media (prefers-color-scheme: dark) {
    :root {
        --gray-900: #ffffff;
        --gray-50: #000000;
        /* ... etc */
    }
}
```

## Performance

- Minimal CSS (~15KB unminified)
- No external dependencies required
- CSS custom properties for efficient customization
- Optimized for quick rendering

## Support

For issues or suggestions, please check the project documentation or create an issue in the repository.

