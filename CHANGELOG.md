Changelog
=====

See VERSIONING (https://github.com/openMF/mifosx/blob/master/VERSIONING.md) for information on what updates to the version number implies for the platform.

Releases
===============
1. 14 July 2013 - 1.5.0.RELEASE
1. 25 Jun 2013 - 1.4.1.RELEASE
1. 20 Jun 2013 - 1.4.0.RELEASE
1. 10 Jun 2013 - 1.3.0.RELEASE
1. 06 Jun 2013 - 1.2.1.RELEASE
1. 27 May 2013 - 1.2.0.RELEASE
1. 24 May 2013 - 1.1.4.RELEASE
1. 22 May 2013 - 1.1.3.RELEASE
1. 19 May 2013 - 1.1.2.RELEASE
1. 12 May 2013 - 1.1.1.RELEASE
1. 09 May 2013 - 1.1.0.RELEASE
1. 08 May 2013 - 1.0.1.RELEASE
1. 07 May 2013 - 1.0.0.RELEASE
1. 30 April 2013 - 0.12.1.beta
2. 30 April 2013 - 0.11.4.beta
1. 22 April 2013 - 0.12.0.beta
1. 17 April 2013 - 0.11.3.beta
1. 10 April 2013 - 0.11.2.beta
1. 05 April 2013 - 0.11.1.beta
1. 05 April 2013 - 0.11.0.beta


1.5.0.RELEASE
=============
Feature (and Bug) Release

This feature (and bug) release focussed on adding support for Holidays and various improvements and bug fixes for Group Functionality

Platform & API

Bug 
 - [MIFOSX-443] - Additional data tables added to groups are appearing for Centers
 - [MIFOSX-471] - Group activation is accepting dates prior to office opening
 - [MIFOSX-491] - In data table, if string length is more than the defined length, no error message is displayed
 - [MIFOSX-493] - For groups, in "Change Loan Application" page not able to modify the "Product" (ie modified product details not getting updated/saved)
 - [MIFOSX-494] - If any changes are made in "Change loan application" (For group loan), the loan cannot be approve (multiple pages popup for single click on "Approve" tab.)
 - [MIFOSX-498] - Searched "Center" is behaving as Group entity
 - [MIFOSX-516] - No/Incorrect error messages on "Register Data Table" screens
 - [MIFOSX-519] - In "add code" page if already existing Code name is entered in the field, no proper error message is displaying
 - [MIFOSX-521] - Not able to disburse the Group Loan
 - [MIFOSX-522] - In add new group form if activation date field is empty, showing connection failure on the UI.
 - [MIFOSX-525] - Not able to select an existing client as Guarantor
 - [MIFOSX-526] - Issues with fields in Add Guarantors Page
 - [MIFOSX-527] - In "New loan application" / "Change loan application" not able to add Collaterals
 - [MIFOSX-528] - For a loan, same guarantor can be associated more than once.
 - [MIFOSX-529] - For the loan under "Pending Approval" stage not able to edit or delete (already added) collaterals.
 - [MIFOSX-531] - Able to approve loans for a Closed client. (ie, client got closed after submitting "new loan application")
 - [MIFOSX-533] - Able to associate closed clients with Groups
 - [MIFOSX-534] - In Client's page not able to assign staff from parent office
 - [MIFOSX-536] - Client search doesn't work at all at present and should support not only account no but also search by any part of client name

New Features & Improvements
 - [MIFOSX-76]  - Support for Holidays
 - [MIFOSX-438] - Allow closing of clients
 - [MIFOSX-486] - Show loanofficers at higher hierarchical level in client product template
 - [MIFOSX-518] - Capture "Repayments rescheduled to" date for a holiday and add processed field
 - [MIFOSX-212] - loan cycle concept for loans
 - [MIFOSX-499] - Inactivate a loan Product
 - [MIFOSX-537] - Ability to display "Note" related to client in Client's page and "note" related to Loan in Loan's page
 - [MIFOSX-475] - Regenerate loan repayment dates if group meeting frequency is changed
 - [MIFOSX-361] - Inconsistent "From Date" and "To Date" formats in View Journal Entry Screens
 - [MIFOSX-362] - Date format is not similar In Balance Sheet as in other pages.
 
Reference App
 - [MIFOSX-535] - Client details screen centers on screen when there are many notes - it should align to top
 - [MIFOSX-309] - Move Global Search to end of the Header
 - [MIFOSX-530] - Connection fail message is displayed by clicking on delete button in Add/Edit code value page
 - [MIFOSX-483] - Support wizard view for Loan Product creation
 - [MIFOSX-476] - Group permissons (in portfolio grouping) makes reference UI grotesque

1.4.1.RELEASE
=============
Bug Release

Platform & API

Bug
 - [MIFOSX-362] - Date format is not similar In Balance Sheet as in other pages.
 - [MIFOSX-473] - "Accounting Rules" created for Branch office (at Head office) is not displaying in head office.
 - [MIFOSX-479] - Scheduled jobs only run for tenant with identifier 'default'
 - [MIFOSX-480] - Not able to "Create A Group" under Branch Office using Head Office staff.
 - [MIFOSX-488] - With DELETE datatables service, one can drop system tables
 
Reference App
 - [MIFOSX-388] - Buttons on table based reports no longer working
 - [MIFOSX-463] - Numeric ids are number formated (causing NumberFormatException )
 - [MIFOSX-467] - Unable to create group in spanish locale as activation date doesnt pick up on culture/locale in datepicker
 - [MIFOSX-481] - Not able to navigate to the immediate previous page from "Clients loan application" page
 - [MIFOSX-482] - In Groups -> Clients loan application page -> no error message is displaying asking for proper inputs
 - [MIFOSX-484] - While creating a "new client" for group (in branch office), Not able to select "Staff" from that (branch) office, if user is Admin.

1.4.0.RELEASE
=============
Feature Release

This feature release focussed on moving groups functionality from BETA to public API. 

Along with Group releated work, key improvements was the addition of a UI and API for adding 'additional data' and staff relationship to clients.

Platform & API

Bug 
 - [MIFOSX-420] - In "Change loan application" not able to save the modifications made because of error message getting displayed on charges.
 - [MIFOSX-424] - In new loan application, loan is getting approved for the Principal amount beyond the range (Minimum and Maximum) defined in the loan product.
 - [MIFOSX-429] - App user of one branch can edit client of another branch
 - [MIFOSX-446] - New loan application is not validating Min-Max Principal defined in the product defination
 - [MIFOSX-447] - New loan application is not validating Min-Max for "# Of Repayments" defined in the product defination
 - [MIFOSX-448] - New loan application is not validating Min-Max "Nominal interest rate %" defined in the product defination
 - [MIFOSX-468] - In "Change loan application" error message is displaying for the second charge which was applied before.
 - [MIFOSX-474] - Adjusting an existing loan transaction fails when 'note' information is passed

New Features & Improvements
 - [MIFOSX-365] - Simplify "Data Tables" functionality
 - [MIFOSX-421] - Ability to create and associate Group Roles
 - [MIFOSX-434] - Allow linking of staff to Clients
 - [MIFOSX-445] - Show loanofficers at higher hierarchical level in product template
 - [MIFOSX-449] - Group and JLG loans repayment schedule should match group meeting dates if loan repayment is synched with group meeting
 - [MIFOSX-457] - Update API param names for accounting rules to be consistent with rest of the accounting services
 - [MIFOSX-458] - Unable to create accounting rules with Tag Id, multiple debits/credits etc
  
Reference App
 - [MIFOSX-378] - TEVI: Produce 'role-based' dashboards as entry/landing page for users that login
 - [MIFOSX-455] - CodeValues functionality broken on the reference Client app
 - [MIFOSX-469] - In Groups unable to activate the created group
 - [MIFOSX-464] - Unable to edit the created charge in "charge details" page
 
BETA (functionality related to BETA functionality
 - [MIFOSX-456] - CRUD services and UI for creating Holidays
 - [MIFOSX-411] - While creating new savings account, If "Interest posting period" is defined Annually, it is getting posted monthly.


1.3.0.RELEASE
=============
Feature Release

This feature release focussed mainly on features requested by Musoni for their upcoming m-lite product and by Quipu technolgies for their TEVI project.

Key changes are improvements to accounting and flexible support for grace concepts in loan repayment schedule.

Platform & API

Bug
 - [MIFOSX-427] - Charges and penalties functionality wonky when multiple charges/penalties are due on the same date

New Features & Improvements
 - [MIFOSX-391] - "Advanced" accounting rules for compound Journal entries
 - [MIFOSX-413] - Ability to account different charges (both fees and penalties) under different heads
 - [MIFOSX-295] - Support Prinicpal and Interest Grace Periods for Loans, Loan Schedules
 - [MIFOSX-297] - Support over payment of loan scenarios common when repayments through mobile, third party
 - [MIFOSX-374] - TEVI: Repayment strategy to support payment ordering of Interest, Principal, Penalties, Fees
 - [MIFOSX-376] - TEVI: Track additional details on the loan repayment schedule
  
Reference App
 - [MIFOSX-405] - In client's General page not able to differentiate between Activated and not Activated savings accounts.
 - [MIFOSX-416] - Distinguish between Individual and JLG loans in clients general details
 - [MIFOSX-418] - Hide/show Centers and center creation based on permissions
 - [MIFOSX-419] - Display Center summary details in UI
 - [MIFOSX-423] - Unable to view group loan in group context
 
BETA (functionality related to BETA functionality
 - [MIFOSX-344] - Add overnight scheduled task to detect if annual fee is due on savings account and apply it
 - [MIFOSX-325] - GSOC 2013 - Savings accounts server side sorting and pagination on retrieve all
 - [MIFOSX-310] - Generate Collection sheet based on meeting calendar dates
 - [MIFOSX-356] - Modify unassign staff API call in group
 - [MIFOSX-69] - Amazon S3 appender for document upload
 - [MIFOSX-395] - TEVI: Support hierarchial staff and organisational roles on staff with linkage to Application User
 
1.2.1.RELEASE
=============
Bug Release

Platform & API

Bug
 - [MIFOSX-402] - Unable to use 'days' period frequency when entering value for 'lockin' on savings product/account
 - [MIFOSX-398] - In savings account - " New saving product" is getting activated before Client activation date.
 - [MIFOSX-399] - If full balance amount is withdrawn from saving account, it is displaying negative balance as withdrawal fee is defined in saving product.
    
Reference App
 - [MIFOSX-358] - Register/Deregister datatables for groups and centers
 - [MIFOSX-396] - Not able to deposit / Withdraw amount in savings account
 - [MIFOSX-401] - While Updating the saving product if withdrawal fee is defined as "0" error message is displayed.
 - [MIFOSX-403] - While defining and updating the saving product if Annual fee is defined as "0" error message is displayed.
 - [MIFOSX-407] - For some transactions by clicking on Deposit/Withdraw, 2-3 popup pages displayed as blank.
 - [MIFOSX-410] - While defining saving product if Annual fee and date text field kept blank it is displaying error. But while editing saving product if Annual fee and date text field kept blank it is accepting.

1.2.0.RELEASE
=============
Feature Release

This feature release is focussed on 'Accounting' improvements that provide greater flexibility in use of accounting for MFIs that use mobile money and wish to be able to track through what channels repayments are disbursements go.

The BETA functionality for savings is now integrated with accounting on platform/api side.

It also contains a number of improvements to the reference app UI.

Platform & API
 - [MIFOSX-223] - Add functionality for Loan Disbursement and Repayment channels/types
 - [MIFOSX-286] - Ability to setup and use Accounting rules for Manual Journal Entries
 - [MIFOSX-338] - Tagging Journal Entries
 - [MIFOSX-392] - Not able to upload .docx (word doccument) and .xlsx (Excel doccument) in Client's "Identity Doccuments" and in "Client Doccuments"
 - [MIFOSX-328] - Loan approval date should not be earlier to the Client activation date.
 - [MIFOSX-246] - Add ability to associate fees with savings product/account
 - [MIFOSX-331] - Add capability to filter the calendars based on Calendar Type.

Reference App
 - [MIFOSX-320] - Beautify Login page
 - [MIFOSX-149] - Add status colors to UI
 - [MIFOSX-147] - Allow ability to produce easy to print loan schedule from the loan screen
 - [MIFOSX-317] - Add Tooltips that explain the significance of mapped "Accounts" in Loan Product Screens
 - [MIFOSX-347] - When creating a new GL Account, pick up on selected category from tree view
 - [MIFOSX-313] - Fix CSS issues with Accounting section of "Define New Loan Products"
 - [MIFOSX-346] - Upgrade Reference UI to JQuery UI 1.10.3 (latest stable version)
 - [MIFOSX-384] - JQuery upgrading breaking the functionality of Add LoanProduct.
 - [MIFOSX-386] - In "Add Loan Product" not able to delete the "Charges"added , similarly in "Accounting" not able to delete "Advanced accounting rules"(ie, Payment Type and Fund Source). Issue may be of latest updation, because in demo version it is working fine.

1.1.4.RELEASE
=============
Bug Release

Platform & API
 - [MIFOSX-387] - Loans template for Client not showing currencies

Reference App
 - [MIFOSX-382] - In Change Loan Application by changing the "Product" name, Details are not getting updated (ie, in Terms & settings),since "currencies" of two loan products are different
 - [MIFOSX-390] - Regression in ability to associate a fund with a loan application through reference app


1.1.3.RELEASE
=============
Bug Release

Bug
 - [MIFOSX-367] - Not able to figure out the loan details, if proper Account no. is entered in Search text field
 - [MIFOSX-368] - To add a new client of client type "corporate" error message is showing "First name is mandatory" and Last name is mandatory" than Full name or Business name.
 - [MIFOSX-369] - Client is getting activated before the date of opening of the related office.
 - [MIFOSX-379] - For applying "New Loan Application", if loan product currency is in US Dollars the charges applied is in Indian Rupees also, it is accepting.
 - [MIFOSX-380] - Smart search on Gaurantors is broken after change of clients api to paginated response

Improvement

  - [MIFOSX-359] - Edit functionality for "Code Values" not intuitive (and inconsistent with rest of the User Interface)
  - [MIFOSX-360] - Inconsistent verbiage for "Cancel" button in "Add/Edit Code Value: " page
  - [MIFOSX-363] - Background is broken in the middle of the table in "Client Loans Listing" page
 
1.1.2.RELEASE
=============
Bug Release
 - MIFOSX-353 - datetime column in custom table report causes violation
 - MIFOSX-364 - New loan application save button restriction

1.1.1.RELEASE
=============
Bug Release
 - MIFOSX-348 - General API concepts do not work for paginated endpoints

1.1.0.RELEASE
=============
Feature Release

Scope of release:

General improvements to display of audit details, pagination of list apis. New api to list loans/loan applications.

In addition to items mentioned below see 1.1.0.RELEASE on JIRA: https://mifosforge.jira.com/browse/MIFOSX/fixforversion/11932 for full details of issues addressed in release.

Core Issues:

- [MIFOSX-334] - Group loses clients when updating
- [MIFOSX-226] - Add ability to API to list loans for applications that require loan centric view of data
- [MIFOSX-339] - Allow retrieving of all items on paginated endpoints

1.0.1.RELEASE
=============
Bug Release
 - MIFOSX-343 - When viewing Audit details, integer values (rather than string values) for officeId and clientId cause exception

1.0.0.RELEASE
=============

The first community sanctioned public release of new mifos platform project ('Mifos X').

Scope of release:

In addition to items mentioned below see 1.0.0.RELEASE on JIRA: https://mifosforge.jira.com/browse/MIFOSX/fixforversion/11833 for full details of issues addressed in release.

Operational Functionality:
 - Client Loan Portfolio Management
 - Cash-based Accounting
 - Audit of all changes
 - Reporting (about 30 Reports)
 - Comprehensive and flexible ability to configure (on request) MFI specific additional client and loan data.

Organisational Functionality:
 - Currency, Funds, Offices, Staff/Loan Officers
 - Loan Products
   - support for declining balance or flat interest methods
   - Ability to configure mfi customised repayment processing
 - Charges
  - Fees & Penalties

User Admin Functionality:
 - Users, Roles, Permissions
 - 4-eye principle / Maker-Checker

0.12.1.beta
==========

Bug Release
 - MIFOSX-311 - Zero % loan products or loan applications are not allowed due to validation checks

0.11.4.beta
==========
Back ported bug fixes in 0.12.1 to 0.11.x series for production installation.

Bug Release
 - MIFOSX-311 - Zero % loan products or loan applications are not allowed due to validation checks

0.12.0.beta
==========

Focus on stabalising API and functionality around centers, groups and clients. Contains breaking changes in this API from the 0.11.x releases.

Note: known issue with existing set of reports provided out of box. Due to changes in client database 
columns for is_deleted (removed), joining_date (changed to activation_date) some of the reports do not work correctly at present. 
We will provide patches to update these.

Bugs
  - [MIFOSX-262] - You can add a client to an office prior to the opening date of the office

Improvements/Features
  - [MIFOSX-213] - Support client and group 'statuses' to enable approval workflows
  - [MIFOSX-282] - Add min max constraints to loan product and enforce for loan application creation, disbursal
  - [MIFOSX-247] - Savings account deposits
  - [MIFOSX-248] - Savings account withdrawals
  - [MIFOSX-290] - Groups api for centers/groups/communal banks
  - [MIFOSX-277] - Add API Documentation for Loan Charges
  - [MIFOSX-285] - Fix Api docs css
  - [MIFOSX-288] - Improve documentation for charges

0.11.3.beta
==========
Bug Release.
  - [MIFOSX-299] - Only super users are able to update client and loan documents

0.11.2.beta
==========
Bug Release.
  - https://mifosforge.jira.com/browse/MIFOSX-292 - Retrieving an existing loan product does not return the correct value for decimalPlaces
  - https://mifosforge.jira.com/browse/MIFOSX-293 - Calculating the Loan Schedule does not use the correct value for decimalPlaces
  - https://mifosforge.jira.com/browse/MIFOSX-294 - Updating loan products min/max principal details incorrectly results in error message relating to interestRatePerPeriod

0.11.1.beta
==========
Bug Release.
  - Revert back to hibernate 4.1.9.Final to allow datatables to be persisted
  - MIFOSX-291 - unable to create office

0.11.0.beta
==========

Focus on Individual Lending with Cash Accounting, in development features such as centers, groups, 
savings accounts and accrual accounting in progress.

Known issues 
   - Due to https://jira.springsource.org/browse/SPR-10395 datatables not persisting correct
   - Due to upgrade to hibernate 4.2, JPA specs more strict causing some entities to not persist correctly (https://mifosforge.jira.com/browse/MIFOSX-291)
