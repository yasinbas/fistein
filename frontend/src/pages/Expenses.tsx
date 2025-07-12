import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Receipt, Search, Filter, Calendar, Users, DollarSign } from 'lucide-react';
import { expensesAPI, groupsAPI } from '../services/api';
import type { Expense, Group } from '../types';

const Expenses: React.FC = () => {
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [groups, setGroups] = useState<Group[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterGroup, setFilterGroup] = useState<string>('all');
  const [sortBy, setSortBy] = useState<'date' | 'amount' | 'title'>('date');
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('desc');

  useEffect(() => {
    const loadExpensesData = async () => {
      try {
        const [expensesData, groupsData] = await Promise.all([
          expensesAPI.getExpenses(),
          groupsAPI.getGroups(),
        ]);

        setExpenses(expensesData);
        setGroups(groupsData);
      } catch (error) {
        console.error('Harcamalar yüklenirken hata:', error);
      } finally {
        setLoading(false);
      }
    };

    loadExpensesData();
  }, []);

  const filteredAndSortedExpenses = expenses
    .filter(expense => {
      const matchesSearch = expense.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                           expense.description?.toLowerCase().includes(searchTerm.toLowerCase());
      
      const matchesGroup = filterGroup === 'all' || expense.group.id.toString() === filterGroup;
      
      return matchesSearch && matchesGroup;
    })
    .sort((a, b) => {
      let comparison = 0;
      
      switch (sortBy) {
        case 'date':
          comparison = new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
          break;
        case 'amount':
          comparison = a.amount - b.amount;
          break;
        case 'title':
          comparison = a.title.localeCompare(b.title);
          break;
      }
      
      return sortOrder === 'asc' ? comparison : -comparison;
    });

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY',
    }).format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('tr-TR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  };

  const getTotalExpenses = () => {
    return filteredAndSortedExpenses.reduce((sum, expense) => sum + expense.amount, 0);
  };

  const getExpensesByGroup = () => {
    const groupExpenses = filteredAndSortedExpenses.reduce((acc, expense) => {
      const groupId = expense.group.id;
      if (!acc[groupId]) {
        acc[groupId] = {
          group: expense.group,
          expenses: [],
          total: 0
        };
      }
      acc[groupId].expenses.push(expense);
      acc[groupId].total += expense.amount;
      return acc;
    }, {} as Record<number, { group: Group; expenses: Expense[]; total: number }>);

    return Object.values(groupExpenses);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Harcamalar</h1>
            <p className="text-gray-600 mt-1">
              Toplam {filteredAndSortedExpenses.length} harcama • {formatCurrency(getTotalExpenses())}
            </p>
          </div>
          <Link
            to="/expenses/new"
            className="btn btn-primary flex items-center gap-2"
          >
            <Plus className="h-5 w-5" />
            Yeni Harcama
          </Link>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
            <input
              type="text"
              placeholder="Harcama ara..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>

          {/* Group Filter */}
          <div className="flex items-center gap-2">
            <Filter className="h-5 w-5 text-gray-400" />
            <select
              value={filterGroup}
              onChange={(e) => setFilterGroup(e.target.value)}
              className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            >
              <option value="all">Tüm Gruplar</option>
              {groups.map(group => (
                <option key={group.id} value={group.id.toString()}>
                  {group.name}
                </option>
              ))}
            </select>
          </div>

          {/* Sort By */}
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as 'date' | 'amount' | 'title')}
            className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
          >
            <option value="date">Tarihe Göre</option>
            <option value="amount">Tutara Göre</option>
            <option value="title">İsme Göre</option>
          </select>

          {/* Sort Order */}
          <select
            value={sortOrder}
            onChange={(e) => setSortOrder(e.target.value as 'asc' | 'desc')}
            className="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
          >
            <option value="desc">Azalan</option>
            <option value="asc">Artan</option>
          </select>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center">
            <Receipt className="h-8 w-8 text-blue-600 mr-3" />
            <div>
              <p className="text-sm text-blue-600">Toplam Harcama</p>
              <p className="text-2xl font-bold text-blue-900">
                {filteredAndSortedExpenses.length}
              </p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center">
            <DollarSign className="h-8 w-8 text-green-600 mr-3" />
            <div>
              <p className="text-sm text-green-600">Toplam Tutar</p>
              <p className="text-2xl font-bold text-green-900">
                {formatCurrency(getTotalExpenses())}
              </p>
            </div>
          </div>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="flex items-center">
            <Users className="h-8 w-8 text-purple-600 mr-3" />
            <div>
              <p className="text-sm text-purple-600">Aktif Gruplar</p>
              <p className="text-2xl font-bold text-purple-900">
                {groups.length}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Group by Groups View */}
      {filterGroup === 'all' && filteredAndSortedExpenses.length > 0 && (
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-6">Gruplara Göre Harcamalar</h2>
          <div className="space-y-6">
            {getExpensesByGroup().map(({ group, expenses, total }) => (
              <div key={group.id} className="border border-gray-200 rounded-lg p-4">
                <div className="flex justify-between items-center mb-4">
                  <div>
                    <h3 className="text-lg font-medium text-gray-900">{group.name}</h3>
                    <p className="text-sm text-gray-600">{expenses.length} harcama</p>
                  </div>
                  <div className="text-right">
                    <p className="text-lg font-semibold text-gray-900">
                      {formatCurrency(total)}
                    </p>
                    <Link
                      to={`/groups/${group.id}`}
                      className="text-primary-600 hover:text-primary-500 text-sm font-medium"
                    >
                      Grup Detayı
                    </Link>
                  </div>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                  {expenses.slice(0, 4).map(expense => (
                    <div key={expense.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                      <div className="flex items-center">
                        <Receipt className="h-4 w-4 text-gray-400 mr-2" />
                        <div>
                          <p className="font-medium text-gray-900 text-sm">{expense.title}</p>
                          <p className="text-xs text-gray-600">{expense.paidBy.fullName}</p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="font-semibold text-gray-900 text-sm">
                          {formatCurrency(expense.amount)}
                        </p>
                        <p className="text-xs text-gray-500">
                          {formatDate(expense.createdAt)}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
                {expenses.length > 4 && (
                  <div className="mt-3 text-center">
                    <Link
                      to={`/groups/${group.id}`}
                      className="text-primary-600 hover:text-primary-500 text-sm font-medium"
                    >
                      +{expenses.length - 4} harcama daha
                    </Link>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* All Expenses List */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-6">
          {filterGroup === 'all' ? 'Tüm Harcamalar' : `${groups.find(g => g.id.toString() === filterGroup)?.name} Harcamaları`}
        </h2>
        
        {filteredAndSortedExpenses.length > 0 ? (
          <div className="space-y-4">
            {filteredAndSortedExpenses.map(expense => (
              <div key={expense.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                <div className="flex justify-between items-start">
                  <div className="flex items-center">
                    <Receipt className="h-6 w-6 text-gray-400 mr-3" />
                    <div>
                      <h3 className="font-semibold text-gray-900">{expense.title}</h3>
                      <p className="text-sm text-gray-600 mt-1">
                        {expense.description || 'Açıklama yok'}
                      </p>
                      <div className="flex items-center gap-4 mt-2 text-sm text-gray-500">
                        <div className="flex items-center">
                          <Users className="h-4 w-4 mr-1" />
                          {expense.group.name}
                        </div>
                        <div className="flex items-center">
                          <Calendar className="h-4 w-4 mr-1" />
                          {formatDate(expense.createdAt)}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-xl font-bold text-gray-900">
                      {formatCurrency(expense.amount)}
                    </p>
                    <p className="text-sm text-gray-600">
                      {expense.paidBy.fullName} ödedi
                    </p>
                    <p className="text-sm text-gray-500">
                      {expense.expenseShares?.length || 0} kişi paylaştı
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-12 text-gray-500">
            <Receipt className="h-16 w-16 mx-auto mb-4 text-gray-300" />
            <h3 className="text-lg font-semibold text-gray-900 mb-2">
              {searchTerm || filterGroup !== 'all' ? 'Harcama bulunamadı' : 'Henüz harcama yok'}
            </h3>
            <p className="text-gray-600 mb-6">
              {searchTerm || filterGroup !== 'all' 
                ? 'Arama kriterlerinize uygun harcama bulunamadı. Farklı filtreler deneyin.'
                : 'Henüz hiç harcama kaydınız bulunmuyor. İlk harcamanızı ekleyin.'
              }
            </p>
            {!searchTerm && filterGroup === 'all' && (
              <Link
                to="/expenses/new"
                className="btn btn-primary inline-flex items-center gap-2"
              >
                <Plus className="h-5 w-5" />
                İlk Harcamayı Ekle
              </Link>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default Expenses;