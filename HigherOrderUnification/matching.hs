--DEFINITIONS

--Variable or constant names are Strings
type Name = String
--Definition of lambda expression
data LambdaExp = Const Name | Local Name | Pattern Name | Apply LambdaExp LambdaExp | Abstr Name LambdaExp deriving Show
--Definition of a rule
data Rule = Rule LambdaExp LambdaExp deriving Show
--Definition of substitutions (identity substitution is defined as a data consturctor for simplicity
data Subst = IdSubst | Subst [(Name, LambdaExp)] deriving Show

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--APPS AND ITS AUXILIARY FUNCTIONS

--Filter all subsets to get subsets with size > 1
filteredSubsets :: Int -> [[Int]]
filteredSubsets n = filter ((>1).length) (subsets [1..n]) 

--Get all subsets
subsets :: [Int] -> [[Int]]
subsets []  = [[]]
subsets (x:xs) = subsets xs ++ map (x:) (subsets xs)

--equivalent of == for lambda expressions
isEqual :: LambdaExp -> LambdaExp -> Bool
isEqual (Const c1) (Const c2) = c1==c2
isEqual (Local v1) (Local v2) = v1==v2
isEqual (Pattern p1) (Pattern p2) = p1==p2
isEqual (Abstr v1 l1) (Abstr v2 l2) = v1==v2 && isEqual l1 l2
isEqual (Apply l11 l12) (Apply l21 l22) = isEqual l11 l21 && isEqual l12 l22
isEqual _ _ = False

--Creates a list of minimal(in terms of application) abstractable lambda terms, which are present in the input expression
createTermList :: LambdaExp -> [LambdaExp]
createTermList (Local v) = []
createTermList (Apply l1 l2) = l1 : l2 : (createTermList l1 ++ createTermList l2)
createTermList t@(Abstr v exp) = t : createTermList exp
createTermList l = []

--Traverses the term list created by the previous function and counts number of occurrences of all term.
--Also deletes duplicate occurrences
countTermList :: [LambdaExp] -> [(LambdaExp,Int)]
countTermList [] = []
countTermList l@(x:xs) = (x,length (filter (isEqual x) l)):countTermList (filter ((not . isEqual x)) xs)

--AppsInner tries to find all (t0,t1) if t has an outer-most application or abstraction
abstractInner :: LambdaExp -> [(LambdaExp,LambdaExp)]
abstractInner (Apply l1 l2) =  abstractFirst l1 l2 ++ abstractSecond l1 l2 ++ formatFirst (abstractInner l1) l2 ++ formatSecond l1 (abstractInner l2)
    where abstractFirst (Local v) _ = []
          abstractFirst l1 l2 = [(Abstr "x" (Apply (Local "x") l2), l1)]

          abstractSecond _ (Local v) = []
          abstractSecond l1 l2 = [(Abstr "x" (Apply l1 (Local "x")), l2)]

          formatFirst [] l2 = []
          formatFirst ((Abstr v exp, t1):xs) l2 = (Abstr v (Apply exp l2), t1):(formatFirst xs l2)

          formatSecond l1 [] = []
          formatSecond l1 ((Abstr v exp, t1):xs) = (Abstr v (Apply l1 exp), t1):(formatSecond l1 xs)

abstractInner (Abstr v0 exp) = formatAbstr (abstractInner exp) v0 exp
    where formatAbstr [] v0 exp = []
          formatAbstr ((Abstr v1 t0, t1):xs) v0 exp = (Abstr v1 (Abstr v0 t0), t1):(formatAbstr xs v0 exp)

abstractInner l = []

--One single abstraction with t0 = λx.x and t1 = t
abstractWhole :: LambdaExp -> (LambdaExp,LambdaExp)
abstractWhole l = (Abstr "x" (Local "x"), l)

--If there are same expressions repeated in the args, we can abstract it in all possible patterns and put local variables at these places.
--These patterns are analogous to all subsets of indices [1..n]
--E.g., if exp is (f a a a), then patterns can be => (λx.fxxx,a),(λx.fxxa,a),(λx.fxax,a),(λx.faxx,a)
--These are subsets of [1,2,3] where size >= 2, hence replace in indices {[1,2],[2,3],[1,3],[1,2,3]}
abstractEquals :: LambdaExp ->[(LambdaExp,LambdaExp)]
abstractEquals l = abstractAll (countTermList (createTermList l))
    where abstractAll [] = []
          abstractAll ((term,cnt):xs) = map (createNewAbstraction term l 1) (filteredSubsets cnt) ++ abstractAll xs

          --Takes a term, replaces all terms in l according to the indices by 'x' (done by 'newEqualsAbstraction' function) and puts an abstraction in front
          createNewAbstraction term l curCnt indices = (Abstr "x" (fst (newEqualsAbstraction term l curCnt indices)), term)

          --This function traverses the original lambda term and places 'x' wherever the term is, according to the indices
          newEqualsAbstraction term l curCnt indices | isEqual term l && (elem curCnt indices) = (Local "x", curCnt + 1)
                                                     | isEqual term l = (l, curCnt + 1)
                                                     | otherwise = patternMatch term l curCnt indices

          --If the current node in original lambda exp is not equal to term, then traverse left, traverse right with updated count and return the final count with final expression
          patternMatch term (Apply l1 l2) curCnt indices = recurseAndFormat (newEqualsAbstraction term l1 curCnt indices) term indices l2
          patternMatch term (Abstr v exp) curCnt indices = (\(t, c) -> (Abstr v t, c)) (newEqualsAbstraction term exp curCnt indices)
          patternMatch term l curCnt indices = (l, curCnt)

          recurseAndFormat (t0, fstCnt) term indices l2 = (\(t1, sndCnt) -> (Apply t0 t1, sndCnt)) (newEqualsAbstraction term l2 fstCnt indices)

--This is a function that takes any lambda expression t and tries to find all possible tuples (t0,t1) such that t0 t1 = t.
--This works by abstracting subexpressions from t in all possible ways.
--Apps is the combination of abstractInner, abstractWhole and abstractEquals
apps :: LambdaExp -> [(LambdaExp,LambdaExp)]
apps l = abstractWhole l : (abstractInner l ++ abstractEquals l)

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--SUBSTITUTION RELATED FUNCTIONS

--Compose two substitutions s1 and s2 and output the resulting substitution
--Composition means apply s2 and then apply s1 => resutantS = s2 ++ (s1 - s2)
composeSubst :: Subst -> Subst -> Subst
composeSubst s IdSubst = s
composeSubst IdSubst s = s
composeSubst (Subst s1) (Subst s2) = Subst (s2 ++ discardPairs s1 (map fst s2))

--calculates (s1-s2) by discarding all pairs from s1 where substitution of some variable p in s1 exists in s2
discardPairs :: [(Name, LambdaExp)] -> [Name] -> [(Name, LambdaExp)]
discardPairs [] vars = []
discardPairs (h@(v,e):xs) vars | elem v vars = discardPairs xs vars
                               | otherwise = h : discardPairs xs vars

--Applies a substitution to a lambda expression
applySubst :: Subst -> LambdaExp -> LambdaExp
applySubst IdSubst l = l
applySubst (Subst s) l@(Const c) = l
applySubst (Subst s) l@(Local v) = l
applySubst (Subst s) (Pattern p) = findAndExtract s p
    where findAndExtract [] p = (Pattern p)
          findAndExtract ((v,e):xs) p | v==p = e
                                      | otherwise = findAndExtract xs p
applySubst sub@(Subst s) (Apply l1 l2) = Apply (applySubst sub l1) (applySubst sub l2)
applySubst sub@(Subst s) (Abstr v l) = Abstr v (applySubst sub l)

--Applies a substitution to a rule
applySubstToRule :: Subst -> Rule -> Rule
applySubstToRule s (Rule p t) = Rule (applySubst s p) t

--Applies a substitution to a set of rules, by applying it individually to every rule
applySubstToRuleSet :: Subst -> [Rule] -> [Rule]
applySubstToRuleSet s [] = []
applySubstToRuleSet s (x:xs) = applySubstToRule s x : applySubstToRuleSet s xs

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--RESOLVE AND ITS AUXILIARY FUNCTIONS

--Check whether the input lambda expression is close?
isClose :: LambdaExp -> Bool
isClose l = isCloseAux l []
    where isCloseAux (Const c) vars = True
          isCloseAux (Local v) vars = elem v vars
          isCloseAux (Pattern p) vars = False
          isCloseAux (Apply l1 l2) vars = isCloseAux l1 vars && isCloseAux l2 vars
          isCloseAux (Abstr v l) vars = isCloseAux l (v:vars)

--Is the lambda expression of type (t0 t1)?
isDividable :: LambdaExp -> Bool
isDividable (Apply t0 t1) = True
isDividable _ = False

--If the expression is dividable, then return (t0,t1) such that t has the structure - Apply t0 t1
divide :: LambdaExp -> [(LambdaExp, LambdaExp)]
divide (Apply t0 t1) = [(t0,t1)]

--Resolve takes a rule and breaks it down to smaller rules along with partial answer in terms of substitutions.
resolve :: Rule -> [(Subst, [Rule])]
resolve (Rule (Local v1) (Local v2)) | v1==v2 = [(IdSubst,[])]
                                     | otherwise = []

resolve (Rule (Pattern p) l) | isClose l = [(Subst [(p,l)],[])]
                             | otherwise = []

resolve (Rule (Const c1) (Const c2)) | c1==c2 = [(IdSubst,[])]
                                     | otherwise = []

resolve (Rule (Abstr v1 p) (Abstr v2 t)) = [(IdSubst,[Rule p t])]

resolve (Rule (Abstr v p) t) = [(IdSubst,[Rule p (Apply t (Local v))])]

resolve (Rule (Apply f e) t) = [(IdSubst,[Rule f t0, Rule e t1]) | (isDividable t), (t0,t1) <- divide t] ++ [(IdSubst,[Rule f t0, Rule e t1]) | (t0,t1) <- apps t] ++ [(IdSubst,[Rule f (Abstr "x" t)])]

resolve _ = []

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--MATHCES - THE MAIN ALGORITHM

--This function takes each rule, breaks it down into smaller rules using 'resolve',
--and finally produces answer substitution by composing each substitution given by 'resolve'.
matches :: [Rule] -> [Subst]
matches [] = [IdSubst]
matches (x:xs) = [composeSubst s1 s2 | (s2,ys) <- resolve x, s1 <- matches (applySubstToRuleSet s2 (ys ++ xs))]

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
