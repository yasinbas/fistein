import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Users, Receipt, TrendingUp, TrendingDown } from 'lucide-react';
import { useAuth } from '../utils/useAuth';
import { authAPI, groupsAPI, expensesAPI } from '../services/api';
import type { Balance, Group, Expense } from '../types';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const [balance, setBalance] = useState<Balance | null>(null);
  const [groups, setGroups] = useState<Group[]>([]);
  const [recentExpenses, setRecentExpenses] = useState<Expense[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        const [balanceData, groupsData, expensesData] = await Promise.all([
          authAPI.getUserBalance(),
          groupsAPI.getGroups(),
          expensesAPI.getExpenses(),
        ]);

        setBalance(balanceData);
        setGroups(groupsData);
        // Show only recent 5 expenses
        setRecentExpenses(expensesData.slice(0, 5));
      } catch (error) {
        console.error('Dashboard verisi yüklenirken hata:', error);
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, []);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY',
    }).format(amount);
  };

  const getBalanceColorClass = (amount: number) => {
    if (amount > 0) return 'balance-positive';
    if (amount < 0) return 'balance-negative';
    return 'balance-zero';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Welcome, Header */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">
          Hoş geldin, {user?.fullName}!
        </h1>
        <p className="text-gray-600">
          Gider takip uygulamanıza genel bakış
        </p>
      </div>

      {/* Balance Overview */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Bakiye Durumu</h2>
        
        {balance ? (
          <div className="text-center">
            <div className="text-3xl font-bold mb-2">
              <span className={getBalanceColorClass(balance.totalBalance)}>
                {formatCurrency(balance.totalBalance)}
              </span>
            </div>
            <div className="flex justify-center mb-4">
              {balance.totalBalance > 0 ? (
                <TrendingUp className="h-6 w-6 text-green-600" />
              ) : balance.totalBalance < 0 ? (
                <TrendingDown className="h-6 w-6 text-red-600" />
              ) : null}
            </div>
            
            {balance.balanceDetails && balance.balanceDetails.length > 0 && (
              <div className="mt-4">
                <h3 className="text-sm font-medium text-gray-700 mb-2">Kişi Bazında Bakiye</h3>
                <div className="space-y-2">
                  {balance.balanceDetails.map((detail, index) => (
                    <div key={index} className="flex justify-between items-center text-sm">
                      <span className="text-gray-600">{detail.user.fullName}</span>
                      <span className={getBalanceColorClass(detail.amount)}>
                        {formatCurrency(detail.amount)}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        ) : (
          <div className="text-center text-gray-500">
            Bakiye bilgisi yüklenemedi
          </div>
        )}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Link
          to="/groups/new"
          className="card hover:shadow-lg transition-shadow duration-200 cursor-pointer"
        >
          <div className="flex items-center">
            <div className="p-3 bg-primary-100 rounded-lg mr-4">
              <Users className="h-6 w-6 text-primary-600" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900">Yeni Grup</h3>
              <p className="text-gray-600 text-sm">Arkadaşlarınızla grup oluşturun</p>
            </div>
          </div>
        </Link>

        <Link
          to="/expenses/new"
          className="card hover:shadow-lg transition-shadow duration-200 cursor-pointer"
        >
          <div className="flex items-center">
            <div className="p-3 bg-green-100 rounded-lg mr-4">
              <Plus className="h-6 w-6 text-green-600" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900">Harcama Ekle</h3>
              <p className="text-gray-600 text-sm">Yeni bir gider kaydı oluşturun</p>
            </div>
          </div>
        </Link>
      </div>

      {/* Groups Overview */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold text-gray-900">Gruplarım</h2>
          <Link
            to="/groups"
            className="text-primary-600 hover:text-primary-500 text-sm font-medium"
          >
            Tümünü gör
          </Link>
        </div>

        {groups.length > 0 ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {groups.slice(0, 3).map((group) => (
              <Link
                key={group.id}
                to={`/groups/${group.id}`}
                className="p-4 border border-gray-200 rounded-lg hover:border-primary-300 hover:shadow-md transition-all duration-200"
              >
                <div className="flex items-center mb-2">
                  <Users className="h-5 w-5 text-gray-400 mr-2" />
                  <h3 className="font-medium text-gray-900 truncate">{group.name}</h3>
                </div>
                <p className="text-sm text-gray-600 mb-2 line-clamp-2">
                  {group.description || 'Açıklama yok'}
                </p>
                <p className="text-xs text-gray-500">
                  {group.members?.length || 0} üye
                </p>
              </Link>
            ))}
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">
            <Users className="h-12 w-12 mx-auto mb-4 text-gray-300" />
            <p>Henüz bir grubunuz yok</p>
            <Link
              to="/groups/new"
              className="text-primary-600 hover:text-primary-500 text-sm font-medium mt-2 inline-block"
            >
              İlk grubunuzu oluşturun
            </Link>
          </div>
        )}
      </div>

      {/* Recent Expenses */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-semibold text-gray-900">Son Harcamalar</h2>
          <Link
            to="/expenses"
            className="text-primary-600 hover:text-primary-500 text-sm font-medium"
          >
            Tümünü gör
          </Link>
        </div>

        {recentExpenses.length > 0 ? (
          <div className="space-y-3">
            {recentExpenses.map((expense) => (
              <div key={expense.id} className="flex items-center justify-between p-3 border border-gray-200 rounded-lg">
                <div className="flex items-center">
                  <Receipt className="h-5 w-5 text-gray-400 mr-3" />
                  <div>
                    <h4 className="font-medium text-gray-900">{expense.title}</h4>
                    <p className="text-sm text-gray-600">
                      {expense.group.name} • {expense.paidBy.fullName}
                    </p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-gray-900">
                    {formatCurrency(expense.amount)}
                  </p>
                  <p className="text-xs text-gray-500">
                    {new Date(expense.createdAt).toLocaleDateString('tr-TR')}
                  </p>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">
            <Receipt className="h-12 w-12 mx-auto mb-4 text-gray-300" />
            <p>Henüz harcama kaydınız yok</p>
            <Link
              to="/expenses/new"
              className="text-primary-600 hover:text-primary-500 text-sm font-medium mt-2 inline-block"
            >
              İlk harcamanızı ekleyin
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;