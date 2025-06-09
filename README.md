
```
Kulkita
├─ .DS_Store
├─ .mvn
│  └─ wrapper
│     └─ maven-wrapper.properties
├─ Dockerfile
├─ docker-compose.yml
├─ firebase-service-account.json
├─ lombok.config
├─ mvnw
├─ mvnw.cmd
├─ nginx.conf
├─ pom.xml
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ com
   │  │     └─ example
   │  │        └─ kulkita
   │  │           ├─ KulkitaApplication.java
   │  │           ├─ config
   │  │           │  ├─ BatchSeederMBG.java
   │  │           │  ├─ CorsConfig.java
   │  │           │  ├─ CorsResponseFilter.java
   │  │           │  ├─ FirebaseConfig.java
   │  │           │  ├─ MenuSeeder.java
   │  │           │  ├─ RestTemplateConfig.java
   │  │           │  ├─ SchedulingConfig.java
   │  │           │  └─ SecurityConfig.java
   │  │           ├─ constants
   │  │           │  ├─ ApiConstants.java
   │  │           │  ├─ BusinessConstants.java
   │  │           │  ├─ ConstantsUsageExample.java
   │  │           │  ├─ IngredientConstants.java
   │  │           │  └─ StorageConstants.java
   │  │           ├─ controller
   │  │           │  ├─ AuthController.java
   │  │           │  ├─ BatchController.java
   │  │           │  ├─ DashboardController.java
   │  │           │  ├─ FifoAnalysisResponse.java
   │  │           │  ├─ FifoController.java
   │  │           │  ├─ MenuController.java
   │  │           │  ├─ NotificationController.java
   │  │           │  ├─ UsageController.java
   │  │           │  └─ WeightScaleController.java
   │  │           ├─ dto
   │  │           │  ├─ request
   │  │           │  │  ├─ AzureMLPredictionRequest.java
   │  │           │  │  ├─ BatchCreateRequest.java
   │  │           │  │  ├─ BatchStatusUpdateRequest.java
   │  │           │  │  ├─ LoginRequest.java
   │  │           │  │  ├─ MLPredictionRequest.java
   │  │           │  │  ├─ ManualUsageRequest.java
   │  │           │  │  ├─ MenuUsageRequest.java
   │  │           │  │  ├─ OtpVerificationRequest.java
   │  │           │  │  ├─ PasswordResetRequest.java
   │  │           │  │  ├─ RegistrationRequest.java
   │  │           │  │  ├─ UsageRecordRequest.java
   │  │           │  │  └─ WeightScaleDataRequest.java
   │  │           │  └─ response
   │  │           │     ├─ AuthResponse.java
   │  │           │     ├─ AvailableIngredientResponse.java
   │  │           │     ├─ AzureMLPredictionResponse.java
   │  │           │     ├─ BatchLabelResponse.java
   │  │           │     ├─ BatchPositionDto.java
   │  │           │     ├─ BatchResponse.java
   │  │           │     ├─ BatchSummaryResponse.java
   │  │           │     ├─ CategorySummaryResponse.java
   │  │           │     ├─ DailyUsageDto.java
   │  │           │     ├─ ErrorResponse.java
   │  │           │     ├─ ExpiryAlertResponse.java
   │  │           │     ├─ FifoBatchResponse.java
   │  │           │     ├─ FifoBatchSummaryDto.java
   │  │           │     ├─ FifoPickingRecommendationResponse.java
   │  │           │     ├─ FifoPriorityDashboardResponse.java
   │  │           │     ├─ FifoStorageRecommendationResponse.java
   │  │           │     ├─ FifoSummaryResponse.java
   │  │           │     ├─ FreshnessStatusSummary.java
   │  │           │     ├─ FreshnessStatusSummaryDto.java
   │  │           │     ├─ FreshnessStatusSummaryResponse.java
   │  │           │     ├─ IngredientLayoutDto.java
   │  │           │     ├─ IngredientSummary.java
   │  │           │     ├─ IngredientSummaryDto.java
   │  │           │     ├─ IngredientSummaryResponse.java
   │  │           │     ├─ MLPredictionResponse.java
   │  │           │     ├─ MenuIngredientResponse.java
   │  │           │     ├─ MenuResponse.java
   │  │           │     ├─ MessageResponse.java
   │  │           │     ├─ NotificationResponse.java
   │  │           │     ├─ NotificationSummaryResponse.java
   │  │           │     ├─ PickingInstructionDto.java
   │  │           │     ├─ PopularMenuDto.java
   │  │           │     ├─ StorageLayoutRecommendationResponse.java
   │  │           │     ├─ StorageLocationSummary.java
   │  │           │     ├─ StorageLocationSummaryDto.java
   │  │           │     ├─ StorageLocationSummaryResponse.java
   │  │           │     ├─ UsageHistoryResponse.java
   │  │           │     ├─ UsageRecordResponse.java
   │  │           │     ├─ UsageStatisticsResponse.java
   │  │           │     ├─ UserInfoResponse.java
   │  │           │     └─ WasteCategoryDto.java
   │  │           ├─ entity
   │  │           │  ├─ Batch.java
   │  │           │  ├─ BatchUsageHistory.java
   │  │           │  ├─ Menu.java
   │  │           │  ├─ MenuIngredient.java
   │  │           │  ├─ Notification.java
   │  │           │  ├─ OtpAttempt.java
   │  │           │  ├─ PasswordResetToken.java
   │  │           │  ├─ User.java
   │  │           │  └─ VerificationToken.java
   │  │           ├─ exception
   │  │           │  ├─ BusinessException.java
   │  │           │  ├─ BusinessValidationException.java
   │  │           │  ├─ EntityNotFoundException.java
   │  │           │  ├─ ExternalServiceException.java
   │  │           │  ├─ GlobalExceptionHandler.java
   │  │           │  ├─ OtpLimitExceededException.java
   │  │           │  ├─ RateLimitExceededException.java
   │  │           │  ├─ ResourceNotFoundException.java
   │  │           │  ├─ SecurityException.java
   │  │           │  ├─ TokenExpiredException.java
   │  │           │  └─ UserAlreadyExistsException.java
   │  │           ├─ repository
   │  │           │  ├─ BatchRepository.java
   │  │           │  ├─ BatchUsageHistoryRepository.java
   │  │           │  ├─ MenuRepository.java
   │  │           │  ├─ NotificationRepository.java
   │  │           │  ├─ OtpAttemptRepository.java
   │  │           │  ├─ PasswordResetTokenRepository.java
   │  │           │  ├─ UserRepository.java
   │  │           │  └─ VerificationTokenRepository.java
   │  │           ├─ security
   │  │           │  ├─ jwt
   │  │           │  │  ├─ AuthTokenFilter.java
   │  │           │  │  └─ JwtUtils.java
   │  │           │  ├─ oauth
   │  │           │  │  └─ CustomOAuth2UserService.java
   │  │           │  └─ services
   │  │           │     ├─ UserDetailsServiceImpl.java
   │  │           │     └─ UserPrincipal.java
   │  │           └─ service
   │  │              ├─ AuthService.java
   │  │              ├─ BatchService.java
   │  │              ├─ DashboardService.java
   │  │              ├─ DashboardSummaryResponse.java
   │  │              ├─ EmailService.java
   │  │              ├─ FifoService.java
   │  │              ├─ FreshnessUpdateService.java
   │  │              ├─ MenuService.java
   │  │              ├─ NotificationService.java
   │  │              ├─ OtpService.java
   │  │              ├─ SpoilagePredictionService.java
   │  │              ├─ UsageService.java
   │  │              ├─ UsageTrendResponse.java
   │  │              ├─ UserLookupService.java
   │  │              ├─ ValidationService.java
   │  │              ├─ WasteAnalysisResponse.java
   │  │              ├─ WeightScaleService.java
   │  │              └─ impl
   │  │                 ├─ AuthServiceImpl.java
   │  │                 ├─ EmailServiceImpl.java
   │  │                 └─ OtpServiceImpl.java
   │  └─ resources
   │     ├─ application-prod.properties
   │     ├─ application.properties
   │     ├─ application.properties.example
   │     └─ templates
   │        ├─ email-verification.html
   │        ├─ password-reset.html
   │        └─ welcome.html
   └─ test
      └─ java
         └─ com
            └─ example
               └─ kulkita
                  └─ KulkitaApplicationTests.java

```