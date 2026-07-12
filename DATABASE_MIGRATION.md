# Database Migration Guide

## Overview
This guide helps migrate existing test data to include the new `organizationId` and `createdBy` fields required by the organization segregation feature.

## Prerequisites
- MongoDB connected and running
- Existing `test_runs` collection with data
- Access to MongoDB shell or MongoDB Compass

## Migration Scenarios

### Scenario 1: Single Organization Migration
If all your existing test runs belong to one organization:

```javascript
// Connect to MongoDB
use test-outcome

// Create the organization first
const orgResult = db.organizations.insertOne({
  name: "Default Organization",
  description: "Migrated from legacy data",
  contactName: "Admin",
  contactEmail: "admin@example.com",
  contactPhone: "+1-000-000-0000",
  status: "ACTIVE"
});

const orgId = orgResult.insertedId.toString();

// Update all test_runs with the organization ID
db.test_runs.updateMany(
  { organizationId: { $exists: false } },
  [
    {
      $set: {
        organizationId: orgId,
        createdBy: "migration"
      }
    }
  ]
);

// Verify the update
db.test_runs.countDocuments({ organizationId: orgId });
```

### Scenario 2: Multi-Organization Migration (Project-Based)
If you have multiple projects that should belong to different organizations:

```javascript
use test-outcome

// Create organizations for each project
const orgs = {
  "project-alpha": db.organizations.insertOne({
    name: "Project Alpha Organization",
    description: "Organization for Project Alpha",
    contactName: "Team Alpha",
    contactEmail: "alpha@example.com",
    status: "ACTIVE"
  }).insertedId.toString(),
  
  "project-beta": db.organizations.insertOne({
    name: "Project Beta Organization",
    description: "Organization for Project Beta",
    contactName: "Team Beta",
    contactEmail: "beta@example.com",
    status: "ACTIVE"
  }).insertedId.toString()
};

// Update test_runs for Project Alpha
db.test_runs.updateMany(
  { project: "project-alpha", organizationId: { $exists: false } },
  [
    {
      $set: {
        organizationId: orgs["project-alpha"],
        createdBy: "migration"
      }
    }
  ]
);

// Update test_runs for Project Beta
db.test_runs.updateMany(
  { project: "project-beta", organizationId: { $exists: false } },
  [
    {
      $set: {
        organizationId: orgs["project-beta"],
        createdBy: "migration"
      }
    }
  ]
);

// Verify updates
db.test_runs.aggregate([
  { $group: { _id: "$organizationId", count: { $sum: 1 } } }
]);
```

### Scenario 3: Manual Project Assignment to Organizations
If you want to manually assign projects to organizations:

```javascript
use test-outcome

// 1. First, create all organizations
const orgIds = {};

orgIds.org1 = db.organizations.insertOne({
  name: "Organization 1",
  description: "First organization",
  status: "ACTIVE"
}).insertedId.toString();

orgIds.org2 = db.organizations.insertOne({
  name: "Organization 2",
  description: "Second organization",
  status: "ACTIVE"
}).insertedId.toString();

// 2. Create mapping of projects to organizations
const projectOrgMapping = {
  "project-alpha": orgIds.org1,
  "project-beta": orgIds.org1,
  "project-gamma": orgIds.org2,
  "project-delta": orgIds.org2
};

// 3. Update test_runs based on mapping
for (const [project, orgId] of Object.entries(projectOrgMapping)) {
  db.test_runs.updateMany(
    { project: project, organizationId: { $exists: false } },
    [
      {
        $set: {
          organizationId: orgId,
          createdBy: "migration"
        }
      }
    ]
  );
}

// 4. Update test_projects with organization IDs
for (const [project, orgId] of Object.entries(projectOrgMapping)) {
  db.test_projects.updateMany(
    { name: project, organizationId: { $exists: false } },
    { $set: { organizationId: orgId } }
  );
}
```

## Verification

After migration, verify the data:

```javascript
use test-outcome

// Check test_runs without organizationId (should be 0)
db.test_runs.countDocuments({ organizationId: { $exists: false } });

// Check test_projects without organizationId (should be 0)
db.test_projects.countDocuments({ organizationId: { $exists: false } });

// View distribution across organizations
db.test_runs.aggregate([
  {
    $group: {
      _id: "$organizationId",
      count: { $sum: 1 },
      projects: { $addToSet: "$project" }
    }
  },
  { $sort: { count: -1 } }
]);

// Check for any missing createdBy fields
db.test_runs.countDocuments({ createdBy: { $exists: false } });
```

## Rollback Plan

If you need to rollback the migration:

```javascript
use test-outcome

// Remove the new fields added during migration
db.test_runs.updateMany(
  { createdBy: "migration" },
  { $unset: { organizationId: "", createdBy: "" } }
);

// Delete created organizations
db.organizations.deleteMany({ description: { $regex: "Migrated|Organization" } });
```

## Using MongoDB Compass (GUI Alternative)

If you prefer using MongoDB Compass GUI:

1. **Connect** to your MongoDB instance
2. **Select** the `test-outcome` database
3. **Select** the `test_runs` collection
4. **Click** the "Aggregation" tab
5. **Create Pipeline**:
   ```json
   [
     {
       "$set": {
         "organizationId": "YOUR_ORG_ID_HERE",
         "createdBy": "migration"
       }
     }
   ]
   ```
6. **Export** results to update or use for verification

## Using Python Script (Alternative)

If you have Python and pymongo installed:

```python
from pymongo import MongoClient
from bson import ObjectId

# Connect to MongoDB
client = MongoClient('mongodb://localhost:27017/')
db = client['test-outcome']

# Create organization
org = db.organizations.insert_one({
    "name": "Default Organization",
    "description": "Migrated from legacy data",
    "status": "ACTIVE"
})

org_id = str(org.inserted_id)

# Update test runs
result = db.test_runs.update_many(
    {"organizationId": {"$exists": False}},
    [
        {
            "$set": {
                "organizationId": org_id,
                "createdBy": "migration"
            }
        }
    ]
)

print(f"Updated {result.modified_count} test runs")

# Verify
count = db.test_runs.count_documents({"organizationId": org_id})
print(f"Total test runs with org: {count}")
```

## Verification Queries

### Check for Unprocessed Records
```javascript
// Test runs without organizationId
db.test_runs.find({ organizationId: { $exists: false } }).count();

// Test projects without organizationId
db.test_projects.find({ organizationId: { $exists: false } }).count();
```

### Organization Statistics
```javascript
// Count test runs per organization
db.test_runs.aggregate([
  { $group: { _id: "$organizationId", count: { $sum: 1 } } },
  { $lookup: {
      from: "organizations",
      localField: "_id",
      foreignField: "_id",
      as: "org_info"
    }
  },
  { $unwind: "$org_info" },
  { $project: { org_name: "$org_info.name", count: 1 } }
]);
```

### Project Distribution
```javascript
// See which projects belong to which organizations
db.test_projects.aggregate([
  { $lookup: {
      from: "organizations",
      localField: "organizationId",
      foreignField: "_id",
      as: "organization"
    }
  },
  { $unwind: "$organization" },
  { $project: {
      project_name: "$name",
      org_name: "$organization.name",
      org_id: "$organizationId"
    }
  },
  { $sort: { org_name: 1 } }
]);
```

## Performance Considerations

For large collections (>100K records):

```javascript
// Create index for faster updates
db.test_runs.createIndex({ "project": 1 });
db.test_runs.createIndex({ "organizationId": 1 });

// Batch update to avoid memory issues
const batchSize = 10000;
const query = { organizationId: { $exists: false } };
let processed = 0;

while (true) {
  const batch = db.test_runs
    .find(query)
    .limit(batchSize)
    .toArray();
  
  if (batch.length === 0) break;
  
  const ids = batch.map(doc => doc._id);
  
  db.test_runs.updateMany(
    { _id: { $in: ids } },
    [{ $set: { organizationId: "org_id_here", createdBy: "migration" } }]
  );
  
  processed += batch.length;
  print(`Processed: ${processed} records`);
}
```

## Testing After Migration

Once migration is complete:

1. **Login** to the application
2. **Navigate** to Dashboard
3. **Verify** test runs are displayed (should not error)
4. **Navigate** to Test History
5. **Verify** projects are listed
6. **Click** on a project
7. **Verify** test runs for that project appear

## Support

If you encounter issues:
1. Check MongoDB logs for errors
2. Verify all documents have `organizationId` field
3. Ensure `organizationId` references valid organization IDs
4. See SETUP_GUIDE.md for troubleshooting


